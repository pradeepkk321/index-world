package com.pk.indexworld;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.indexworld.indexer.models.Collection;

/**
 * 
 * This utility helps to handle JSON data like converting JSON String into
 * Map/List and vice-versa, reading JSON file from resource folder or a
 * specified path and converting it into Map/List.
 * 
 * @author Pradeep Kumara K
 * 
 */
public interface JSONUtil {

	/**
	 * Converts JSON String into HashMap.
	 * 
	 * @param json JSON String
	 * @return Map
	 */
	public static Map<String, Collection> jsonToMap(String json) {
		HashMap<String, Collection> map = new HashMap<String, Collection>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert JSON to map
			map = (HashMap<String, Collection>) mapper.readValue(json, new TypeReference<Map<String, Collection>>() {
			});

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * Converts JSON String into ArrayList.
	 * 
	 * @param json JSON String
	 * @return List
	 */
	public static List<Object> jsonToList(String json) {

		List<Object> list = new ArrayList<Object>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert JSON to List
			list = mapper.readValue(json, new TypeReference<List<Object>>() {
			});

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * Converts list into JSON String.
	 * 
	 * @param list Any of the List implementation
	 * @return JSON String
	 */
	public static String listToJson(List<Object> list) {
		String json = null;
		ObjectMapper mapperObj = new ObjectMapper();
		try {
			json = mapperObj.writeValueAsString(list);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}

	/**
	 * Converts map into JSON String.
	 * 
	 * @param map Any of the Map implementation
	 * @return JSON String
	 */
	public static String mapToJson(Map<String, Collection> map) {

		String json = null;
		ObjectMapper mapperObj = new ObjectMapper();
		try {
			json = mapperObj.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}
	
	public static String mapToPrettyJson(Map<String, Object> map) {

		String json = null;
		ObjectMapper mapperObj = new ObjectMapper();
		try {
			json = mapperObj.writerWithDefaultPrettyPrinter().writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return json;
	}
	
	public static String objToPrettyJson(Object obj) {

		String json = null;
		ObjectMapper mapperObj = new ObjectMapper();
		
		Map<String, Object> map = mapperObj.convertValue(obj, Map.class);
		
		return mapToPrettyJson(map);

	}
	
	/**
	 * Reads a JSON file from Resource folder and converts into JSON String
	 * 
	 * @param resourceName Name of the JSON file
	 * @return JSON String
	 * @throws IOException
	 */
	public static String resourceFileToJsonString(String resourceName) throws IOException {

		File templateFile;
		templateFile = new ClassPathResource(resourceName).getFile();
		return new String(Files.readAllBytes(templateFile.toPath()));

	}

	/**
	 * Reads a JSON file from Resource folder and converts into Map
	 * 
	 * @param resourceName Name of the JSON file
	 * @return Map
	 * @throws IOException
	 */
	public static Map convertResourceJSONFileToMap(String resourceName) throws IOException {
		return jsonToMap(resourceFileToJsonString(resourceName));
	}

	/**
	 * Reads a JSON file from Resource folder and converts into List
	 * 
	 * @param resourceName Name of the JSON file
	 * @return List
	 * @throws IOException
	 */
	public static List convertResourceJSONFileToList(String resourceName) throws IOException {
		return jsonToList(resourceFileToJsonString(resourceName));
	}

}
