package com.pk.indexworld.indexer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.pk.indexworld.JSONUtil;

public class Indexer {

	static Map docIndex = new HashMap(999999);

	static Map<String, Object> wordIndex = new HashMap<String, Object>(999999);

	public static Map getDocIndex() {
		return docIndex;
	}

	public static Map<String, Object> getWordIndex() {
		return wordIndex;
	}

	public static void indexDocument(Map doc, Map indexMetadata) {

		UUID uuid = UUID.randomUUID();
		docIndex.put(uuid, doc);
		indexWords(uuid, doc, indexMetadata);
	}

	private static void indexWords(UUID uuid, Map doc, Map indexMetadata) {

		List<String> indexElements = (List) indexMetadata.get("indexElements");

		for (String element : indexElements) {
			if (doc.containsKey(element)) {
				String data = (String) doc.get(element);

				if (data != null) {
					String[] words = data.split(" ");

					for (String word : words) {
						word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
						Set references = null;
						
							if (wordIndex.containsKey(word)) {
								references = (Set) wordIndex.get(word);
								references.add(uuid);
							} else {
								references = new HashSet<>();
								references.add(uuid);
								wordIndex.put(word, references);
							}	
						
					}

//					for (int i = 0; i < words.length; i++) {
//
//						String multiWord = words[i];
//						multiWord = multiWord.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//
//						updateWordIndex(uuid, multiWord);
//
//						for (int j = i + 1; j < 10 && j < words.length; j++) {
//
//							String nextWord = words[j];
//							nextWord = nextWord.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
//
//							multiWord = multiWord + "+" + nextWord;
//
//							updateWordIndex(uuid, multiWord);
//						}
//
//					}
				}

			}
		}
	}

	private static void updateWordIndex(UUID uuid, String word) {
		Set references = null;
		if (wordIndex.containsKey(word)) {
			references = (Set) wordIndex.get(word);
			references.add(uuid);
		} else {
			references = new HashSet<>();
			references.add(uuid);
			wordIndex.put(word, references);
		}
	}

	public static void prindIndexData() {
		System.out.println("----------------------");
		System.out.println(JSONUtil.mapToJson(docIndex));
		System.out.println("----------------------");
//		System.out.println(JSONUtil.mapToJson(wordIndex));
		System.out.println("----------------------");
	}

	public static List seach(String data) {

		List result = new ArrayList<Map>();

		Set<UUID> allReferences = new HashSet();

		if (data != null) {

			String[] words = data.split("");

			for (String word : words) {
//				word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();

				if (wordIndex.containsKey(word)) {
					Set references = (Set) wordIndex.get(word);
					allReferences.addAll(references);
				}
			}
			for (UUID uuid : allReferences) {
				result.add((Map) docIndex.get(uuid));
			}
		}
		return result;
	}

	public static List advancedSearch(String query) {
		List result = new ArrayList<Map>();

		LinkedHashSet<UUID> indexes = findAllIndexes(query);

		for (UUID index : indexes) {
			result.add(docIndex.get(index));
		}

		return result;
	}

	private static LinkedHashSet<UUID> findAllIndexes(String query) {

		LinkedHashSet<UUID> indexes = new LinkedHashSet<>();

		findQueryIndexes(query, indexes);

		return indexes;
	}

	public static void findQueryIndexes(String query, LinkedHashSet<UUID> indexes) {

		System.out.println("Getting result for Query: " + query);

		String[] words = query.split(" ");

		int length = words.length;

		for (int i = 1; i <= length; i++) {

			int diff = length - i;

			for (int j = 0; j < i; j++) {

				String s = "";

				for (int k = j; k <= j + diff; k++) {
					if (s.equalsIgnoreCase(""))
						s += words[k];
					else
						s += "+" + words[k];
				}

				System.out.println("new query: " + s);

				if (wordIndex.containsKey(s)) {
					Set references = (Set) wordIndex.get(s);
					indexes.addAll(references);
				}

			}

		}

	}

}
