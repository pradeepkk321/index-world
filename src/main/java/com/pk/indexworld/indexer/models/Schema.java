package com.pk.indexworld.indexer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Schema class helps define schema for the new {@link Collection}
 * 
 * @author Pradeep Kumara K
 *
 */
public class Schema {

	// Default id field
	static final String DEFAULT_ID = "id";

	// Default Schema object
	static Schema defaultSchema;

	public static class SchemaDefinition {

		String field;

		String dataType;

		boolean indexRequired;
		
		boolean multiValue;

		SchemaDefinition(String field, String dataType, boolean indexRequired, boolean multiValue) {
			super();
			this.field = field;
			this.dataType = dataType;
			this.indexRequired = indexRequired;
			this.multiValue = multiValue;
		}

		public SchemaDefinition() {
			super();
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getDataType() {
			return dataType;
		}

		public void setDataType(String dataType) {
			this.dataType = dataType;
		}

		public boolean isIndexRequired() {
			return indexRequired;
		}

		public void setIndexRequired(boolean indexRequired) {
			this.indexRequired = indexRequired;
		}

		public boolean isMultiValue() {
			return multiValue;
		}

		public void setMultiValue(boolean multiValue) {
			this.multiValue = multiValue;
		}

	}

	String name;

	List<SchemaDefinition> definitions;

	boolean isDefault = false;

	String idField;
	
	String idType;

	public Schema(String name, List<SchemaDefinition> definitions) {
		super();
		this.name = name;
		this.definitions = definitions;
	}

	private Schema() {
		this.idField = DEFAULT_ID;
		this.idType = "string";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<SchemaDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<SchemaDefinition> definitions) {
		this.definitions = definitions;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField != null ? idField : DEFAULT_ID;
	}

	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType != null ? idType: "string";
	}

	/**
	 * Adds new SchemaDefinition.
	 * 
	 * @param field         Name of the field
	 * @param dataType      Datatype of the field
	 * @param indexRequired Should index the field
	 * @return new SchemaDefinition
	 */
	public SchemaDefinition addDefinition(String field, String dataType, boolean indexRequired, boolean multiValue) {

		SchemaDefinition definition = new SchemaDefinition(field, dataType, indexRequired, multiValue);
		if (this.definitions == null)
			this.definitions = new ArrayList<Schema.SchemaDefinition>();
		this.definitions.add(definition);

		return definition;
	}

	/**
	 * Creates if not exists a new Default Schema Object and with the idField set to
	 * "id". <br/>
	 * Invoked when no schema is provided.
	 * 
	 * @return Default Schema Object.
	 */
	public static Schema getDefaultSchema() {
		if (defaultSchema == null) {
			defaultSchema = new Schema();
			defaultSchema.setIdField(DEFAULT_ID);
			defaultSchema.setDefault(true);
		}

		return defaultSchema;
	}

}
