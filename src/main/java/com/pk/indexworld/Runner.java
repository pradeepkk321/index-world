package com.pk.indexworld;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.pk.indexworld.indexer.Indexer;

public class Runner {
	

	public static void main(String[] args) throws IOException {
		
//		String s1 = "abecde";
//
//		System.out.println(s1.indexOf("e"));
//		System.out.println(s1.lastIndexOf("e"));
//		System.out.println(s1.indexOf("c"));
//		System.out.println(s1.lastIndexOf("c"));
//		System.out.println(s1.indexOf("f"));
//		System.out.println(s1.lastIndexOf("e"));
		
//		Indexer.findQueryIndexes("hhhh block a industrial", new LinkedHashSet<>());
		
		
//		System.out.println(testSet.containsAll(Arrays.asList("and", "a", "21")));
		
		
		List<Map> employees = JSONUtil.convertResourceJSONFileToList("EmployeeData.json");
		
		Map metadata = new HashMap<>();
		metadata.put("indexElements", Arrays.asList("name", "email", "liveLocation"));
		
		for(Map employee: employees) {
			
			Indexer.indexDocument(employee, metadata);
		}
		
		Indexer.prindIndexData();
		
//		System.out.println("------------------------------------------------------------");
//		System.out.println("Search Result for: noidya sector 63 Vidya polymer");
//		System.out.println(JSONUtil.listToJson(Indexer.advancedSearch("noidya sector 63 Vidya polymer")));
//		System.out.println("------------------------------------------------------------");
		System.out.println("Search Result for: Patel");
		System.out.println(JSONUtil.listToJson(Indexer.seach("Patel")));
		System.out.println("------------------------------------------------------------");
		System.out.println("Search Result for: Manresh");
		System.out.println(JSONUtil.listToJson(Indexer.seach("Manresh")));
		
	}
}
