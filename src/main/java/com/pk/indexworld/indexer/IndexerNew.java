package com.pk.indexworld.indexer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.pk.indexworld.JSONUtil;
import com.pk.indexworld.indexer.models.Collection;
import com.pk.indexworld.indexer.models.Schema;
import com.pk.indexworld.indexer.models.Schema.SchemaDefinition;
import com.pk.indexworld.preprocesser.WordPreProcessor;

import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.algorithms.DefaultStringFunction;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class IndexerNew {
	
	private final static Logger logger = LoggerFactory.getLogger(IndexerNew.class);

	public static Map<String, Collection> database = new HashMap<>();
	
	// file system to store and retrieve data in case of restart
	private static final String filepath="D:\\My Workspace\\Java\\Local\\index-world\\files\\index-world5.json";
	
	// static block to load the data on start up
	static {
		reloadData();
	}

	// Creates a new collection
	public static String createCollection(Schema schema) {

		if (schema.getName() != null) {
			String schemaName = schema.getName().toLowerCase();

			if (schemaName.length() > schemaName.replaceAll("[^a-zA-Z0-9]", "").length())
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schema name should not contain special characters");

			if (database.containsKey(schemaName))
				throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate collection name!!!");
			
			database.put(schemaName.toLowerCase(), new Collection(schemaName, schema));
			
			// Backup when a new collection is created.
			backupData(JSONUtil.mapToJson(database));
			return schemaName;
		}
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schema name not found");

	}

	// Saves a single document
	public static void save(String collectionName, Map document, boolean isBulkRequest) {
		if (database.containsKey(collectionName)) {
			
			Collection collection = database.get(collectionName);
			
			Map<String, Map> documents = collection.getDocuments();
			Map<String, HashSet> indexes = collection.getIndexes();
			Schema schema = collection.getSchema();
			Map<String, HashMap<String, Integer>> indexesWithWordCount = collection.getIndexesWithWordCount();
			
			String documentId = document.get(schema.getIdField()).toString();
			
			// If the document already present in the database remove all the old indexes as
			// the new document may not contain all the words in the previous version.
			if (documents.containsKey(documentId))
				removeOldIndexes(documents, indexes, schema);
			
			documents.put(documentId, document);

			indexDocument(documentId, document, indexes, indexesWithWordCount, schema);
			
			// Backup when new document is added
			if(!isBulkRequest)
				backupData(JSONUtil.mapToJson(database));

		} else
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Collection: " + collectionName + " not found!!!");
	}
	
	// Saves bulk data into the collection
	public static void save(String collectionName, List<Map> documents) {
		for(Map document: documents)
			save(collectionName, document, true);
		backupData(JSONUtil.mapToJson(database));
	}
	
	public static Map seach(String collectionName, String searchQuery, Integer pageNo, Integer pageSize) {
		
		boolean queryModified = false;
		StringBuilder actualQuery = new StringBuilder();
		
		Map response = new HashMap();
		
		logger.info("Searching Query: "+searchQuery);

		List result = new ArrayList<Map>();
		Set<String> allReferences = new HashSet();
		
		List<Set> list = new ArrayList<>();

		if (database.containsKey(collectionName)) {

			Collection collection = database.get(collectionName);

			Map<String, Map> documents = collection.getDocuments();
			Map<String, HashSet> indexes = collection.getIndexes();

			if (searchQuery != null) {
				
		        Map<Object, Object> metaData = new HashMap<>();
		        int totalRecords = 0;

				if (searchQuery.equals("*")) {
					for (String id : documents.keySet()) {
						result.add((Map) documents.get(id));
					}
					
//					result.addAll(documents.keySet());
					totalRecords = result.size();
				} else {

					String[] searchWords = searchQuery.split(" ");

//					for (String word : words) {
//						word = word.toLowerCase();
//
//						if (indexes.containsKey(word)) {
//							Set references = (Set) indexes.get(word);
//							allReferences.addAll(references);
//						}
//					}
//					for (String id : allReferences) {
//						result.add((Map) documents.get(id));
//					}
					
					// Finding all the references for all the words
					for(String word: searchWords) {
//						word = word.toLowerCase();
						word = WordPreProcessor.apply(word);
						
						if (indexes.containsKey(word)) {
							Set references = (Set) indexes.get(word);
							list.add(references);
						} else {
							
//							List<ExtractedResult> extractSorted = FuzzySearch.extractSorted("javafuzzysearch", Arrays.asList("h","thrl", "iler", "fuzzy", "search", "g", "iller", "thriller", "li", "trier"), new AdvancedWeightedRatio());
//							System.out.println("fuzzysearch ----------------->" +extractSorted);
							
							ExtractedResult extractOne = FuzzySearch.extractOne(word, indexes.keySet(), new AdvancedWeightedRatio());
							if(null != extractOne) {
								
								queryModified = true;
								
								System.out.println("Fuzzy match for '"+word+"' is '"+extractOne.getString());
								
								word = extractOne.getString();
								
								Set references = (Set) indexes.get(word);
								list.add(references);
							}
							
						}
						
						actualQuery.append(" ").append(word);
					}
					
					actualQuery.deleteCharAt(0);
					
					// Grouping all references by counts. key is reference and value is the no of
					// search words the reference document contains
					Map<String, Long> referenceWordCounts = (Map<String, Long>) list.stream().flatMap(Set::stream)
							.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
					System.out.println("Reference word counts: "+referenceWordCounts);
					
					// Creating a list of the above map to sort the map based on count
			        List<Map.Entry<String, Long> > referenceWordCountList =
			               new ArrayList<Map.Entry<String, Long> >(referenceWordCounts.entrySet());
					
					// Sorting the list based on word count. References with most words will appear first.
			        Collections.sort(referenceWordCountList, new Comparator<Map.Entry<String, Long> >() {
			            public int compare(Map.Entry<String, Long> o1,
			                               Map.Entry<String, Long> o2)
			            {
			                return (o2.getValue()).compareTo(o1.getValue());
			            }
			        });
			        
//			        for(Map.Entry<String, Long> entry : referenceWordCountList) {
//			        	System.out.println("reference=noOfWords : "+entry);
//			        	result.add((Map) documents.get(entry.getKey()));
//			        }

			        totalRecords = referenceWordCountList.size();
			        int startIndex = 0;
			        int endIndex = totalRecords;

			        if(pageNo != null && pageNo > 0 && pageSize != null && pageSize > 0) {
			        	
			        	startIndex = (pageNo - 1) * pageSize;
			        	endIndex = startIndex + pageSize;
			        	
						int totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize
								: totalRecords / pageSize + 1;
						boolean isFirstPage = pageNo == 1;
						boolean isLastPage = endIndex >= totalRecords;
						
						if(!isFirstPage) {
							String 	prev = "http://localhost:8000/" + collectionName + "/search?query=" + searchQuery + "&pageNo=" + (pageNo-1) + "&pageSize=" +pageSize;	
							metaData.put("prev", prev);
						}
						if(!isLastPage) {
							String 	next = "http://localhost:8000/" + collectionName + "/search?query=" + searchQuery + "&pageNo=" + (pageNo+1) + "&pageSize=" +pageSize;
							metaData.put("next", next);
						}

			        	
						metaData.put("pageNo", pageNo);
						metaData.put("pageSize", pageSize);
						metaData.put("totalPages", totalPages);
						metaData.put("isFirstPage", isFirstPage);
						metaData.put("isLastPage", isLastPage);
			        }
			        

					for (int i = startIndex; i < endIndex && i < totalRecords; i++) {
			        	Map.Entry<String, Long> entry = referenceWordCountList.get(i);
			        	System.out.println("reference=noOfWords : "+entry);
			        	result.add((Map) documents.get(entry.getKey()));
			        }
			        

				}
				
				metaData.put("searchQuery", searchQuery);
				metaData.put("actualQuery", actualQuery.toString());
				metaData.put("queryModified", queryModified);
				metaData.put("currentRecords", result.size());
				metaData.put("totalRecords", totalRecords);
		        response.put("data", result);
		        response.put("metaData", metaData);
			}
		}
		
		
		
		return response;
	}

	private static void removeOldIndexes(Map<String, Map> documents, Map<String, HashSet> indexes, Schema schema) {

		// Need to implement
		
	}

	private static void indexDocument(String documentId, Map document, Map<String, HashSet> indexes, Map<String, HashMap<String, Integer>> indexesWithWordCount, Schema schema) {

		List<String> indexFields = schema.getDefinitions().stream().filter(definition -> definition.isIndexRequired())
				.map(definition -> definition.getField()).collect(Collectors.toList());
		
		for (SchemaDefinition definition : schema.getDefinitions()) {
			if (definition.isIndexRequired()) {
				if (definition.isMultiValue()) {
					List<String> dataSet = (List<String>) document.get(definition.getField());
					for (String data : dataSet) {
						indexWords(documentId, indexes, indexesWithWordCount, data);
					}
				} else {
					String data = (String) document.get(definition.getField());
					indexWords(documentId, indexes, indexesWithWordCount, data);
				}
			}
		}
	}

	private static void indexWords(String documentId, Map<String, HashSet> indexes, Map<String, HashMap<String, Integer>> indexesWithWordCount, String data) {

		if(data != null) {
			String[] words = data.split(" ");

			for (String word : words) {
//				word = word.toLowerCase();
//				
//				int wordLength = word.length();
				
				word = WordPreProcessor.apply(word);
//				if(word.charAt(wordLength-1) == '.' || word.charAt(wordLength-1) == ',')
//					word = word.substring(0, wordLength-1);
				if (indexes.containsKey(word)) {
					HashSet references = (HashSet) indexes.get(word);
					references.add(documentId);
				} else {
					HashSet references = new HashSet<>();
					references.add(documentId);
					indexes.put(word, references);
				}
				
//				if (indexesWithWordCount.containsKey(word)) {
//					
//					HashMap<String, Integer> references = indexesWithWordCount.get(word);
//					
//					if(references.containsKey(documentId))
//						references.put(documentId, references.get(documentId)+1);
//					else
//						references.put(documentId, 1);
//					
//				} else {
//					
//					HashMap<String, Integer> references = new HashMap<>();
//					references.put(documentId, 1);
//					
//					indexesWithWordCount.put(word, references);
//				}
			}			
		}
	}
	
	// returns entire database
	public static Map allData() {
		return database;
	}
 
//    public static void backupData(Object serObj) {
// 
//        try {
//        	
//            FileOutputStream fileOut = new FileOutputStream(filepath);
//            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
//            objectOut.writeObject(serObj);
//            objectOut.close();
//            System.out.println("The Object  was succesfully written to a file");
// 
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
    
	// Backup data into file system
    public static void backupData(String text) {
    	 
        try (PrintStream out = new PrintStream(new FileOutputStream(filepath))) {
            out.print(text);
        } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    // Reload the data from file system
    public static void reloadData() {
    	logger.info("Loading data...");
		try {
			String data = new String(Files.readAllBytes(Paths.get(filepath)));
	    	database = JSONUtil.jsonToMap(data);
		} catch (IOException e) {
			logger.error("Failed to load data. Exception details: "+e.getMessage());
		}
    }
    
    // Clears entire database
    public static void clearAll() {
    	database.clear();
    	backupData(JSONUtil.mapToJson(database));
    	logger.info("Cleared database");
    }
    
    // drops a collection
    public static void dropCollection(String collectionName) {
		if (database.containsKey(collectionName)) {
			database.remove(collectionName);
			backupData(JSONUtil.mapToJson(database));
			logger.info("Dropped collection: " + collectionName);
		}
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid collection name: "+collectionName);

    }
    
    // clears data of a collection
    public static void clearData(String collectionName) {
    	if (database.containsKey(collectionName)) {
			Collection collection = database.get(collectionName);
			collection.getDocuments().clear();
			collection.getIndexes().clear();
			collection.getIndexesWithWordCount().clear();
			backupData(JSONUtil.mapToJson(database));
			logger.info("cleared data for collection: " + collectionName);
		}
		else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid collection name: "+collectionName);
    }
    
    
    // UI Related Code
    
    public static List indexPage() {

    	Map response = new HashMap<>();
    	
    	Set<String> collectionNames = database.keySet();
    	List<Collection> collections = new ArrayList<>();
    	
    	for(String collection: collectionNames) {
    		
    		collections.add(database.get(collection));
//    		Schema schema = database.get(collection).getSchema();
//    		Map<String, Map> documents = database.get(collection).getDocuments();
//    		Map<String, HashSet> indexes = database.get(collection).getIndexes();
    		
    	}
    	
    	return collections;
    }
    
    public static String document1(String name, String id) {
    	
    	return JSONUtil.mapToPrettyJson(database.get(name).getDocuments().get(id));
    	
    }
    
    public static Map document(String name, String id) {
    	

    	
    	return database.get(name).getDocuments().get(id);
    	
    }
    
    public static String schema(String name) {
    	
    	return JSONUtil.objToPrettyJson(database.get(name).getSchema());
    	
    }

}
