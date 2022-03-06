package com.pk.indexworld.indexer.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Collection {

	String name;
	
	Schema schema;
	
	Map<String, Map> documents;
	
	Map<String, HashSet> indexes;
	
	Map<String, HashMap<String, Integer>> indexesWithWordCount;
	
	public Collection() {
	}
	
	public Collection(String name) {
		this(name, Schema.getDefaultSchema());
	}

	public Collection(String name, Schema schema) {
		super();
		this.name = name;
		this.schema = schema;
		this.documents = new HashMap<>(99999);
		this.indexes = new HashMap<>(99999);
		this.indexesWithWordCount = new HashMap<>(99999);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public Map<String, Map> getDocuments() {
		return documents;
	}

	public void setDocuments(Map<String, Map> documents) {
		this.documents = documents;
	}

	public Map<String, HashSet> getIndexes() {
		return indexes;
	}

	public void setIndexes(Map<String, HashSet> indexes) {
		this.indexes = indexes;
	}

	public Map<String, HashMap<String, Integer>> getIndexesWithWordCount() {
		return indexesWithWordCount;
	}

	public void setIndexesWithWordCount(Map<String, HashMap<String, Integer>> indexesWithWordCount) {
		this.indexesWithWordCount = indexesWithWordCount;
	}

	@Override
	public String toString() {
		return "Collection [name=" + name + ", schema=" + schema + ", documents=" + documents + ", indexes=" + indexes
				+ "]";
	}
	
}
