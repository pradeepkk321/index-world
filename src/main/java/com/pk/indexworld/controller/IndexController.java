package com.pk.indexworld.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pk.indexworld.indexer.ChatBot;
import com.pk.indexworld.indexer.IndexerNew;
import com.pk.indexworld.indexer.models.Schema;

@RestController
public class IndexController {

	/* Database level APIs */
	
	@GetMapping("/database")
	public Map allData() {
		return IndexerNew.allData();
	}
	
	@GetMapping("/reload")
	public String reloadData() {
		IndexerNew.reloadData();
		return "success";
	}
	
	@GetMapping("/clearAll")
	public String clearDatabase() {
		IndexerNew.clearAll();
		return "success";
	}

	@PostMapping("/collection/create")
	public Map createCollection(@RequestBody Schema schema, HttpServletRequest request) {

		String schemaName = IndexerNew.createCollection(schema);

		String collectionUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + "/" + schemaName;

		Map response = new HashMap<>();
		response.put("message", "Collection: " + schemaName + " created successfully");

		Map apiList = new HashMap<>();
		apiList.put("Search", collectionUrl + "/search");
		apiList.put("Save Document", collectionUrl + "/save");
		apiList.put("Bulk Data Save", collectionUrl + "/bulkdata");
		apiList.put("Drop Collection", collectionUrl + "/drop");
		apiList.put("Clear Collection", collectionUrl + "/clear");

		response.put("apiList", apiList);

		return response;
	}

	@PostMapping("/{collection}/save")
	public String saveDocument(@PathVariable String collection, @RequestBody Map document) {
		IndexerNew.save(collection, document, false);
		return "success";
	}
	
	@PostMapping("/{collection}/bulkdata")
	public String saveBulkDocuments(@PathVariable String collection, @RequestBody List<Map> documents) {
		IndexerNew.save(collection, documents);
		return "success";
	}

	@GetMapping("/{collection}/search")
	public Map searchData(@PathVariable String collection, @RequestParam String query, @RequestParam(required = false) Integer pageNo, @RequestParam(required = false)Integer pageSize) {
		
		return IndexerNew.seach(collection, query, pageNo, pageSize);
	}
	
	@PostMapping("/{collection}/drop")
	public String dropCollection(@PathVariable String collection) {
		IndexerNew.dropCollection(collection);
		return "success";
	}
	
	@PostMapping("/{collection}/clear")
	public String clearData(@PathVariable String collection) {
		IndexerNew.clearData(collection);
		return "success";
	}
	
	@GetMapping("/")
	public List chatBot() {
		return ChatBot.data();
	}

}
