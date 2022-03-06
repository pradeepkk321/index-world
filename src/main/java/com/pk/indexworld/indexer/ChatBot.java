package com.pk.indexworld.indexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pk.indexworld.JSONUtil;

public class ChatBot {

	private static final String dataPath = "D:\\My Workspace\\Data\\ambignq_light\\dev_light.json";
	
	
	public static Map<String, Object> database = new HashMap<>();
	
	public static List<Object> dataSet = new ArrayList<>();
	
	// static block to load the data on start up
//		static {
//			loadData();
//		}
		
		public static List data() {
			return dataSet;
		}
		
	
	// Reload the data from file system
    public static void loadData() {
		try {
			String data = new String(Files.readAllBytes(Paths.get(dataPath)));
			dataSet = JSONUtil.jsonToList(data);
		} catch (IOException e) {
			System.err.println("Exception occured: "+e);
		}
    }
    
	public static Map<String, Object> jsonToMap(String json) {
		HashMap<String, Object> map = new HashMap<>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			// Convert JSON to map
			map = (HashMap<String, Object>) mapper.readValue(json, new TypeReference<Map<String, Object>>() {
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
}
