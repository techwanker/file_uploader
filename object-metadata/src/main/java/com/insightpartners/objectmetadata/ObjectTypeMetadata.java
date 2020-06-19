package com.insightpartners.objectmetadata;

import java.util.LinkedHashMap;

/**
 * Metadata about an object type.
 * 
 * @author jjs
 * 
 */
public class ObjectTypeMetadata {
	/**
	 * Fields in the object keyed by field id.
	 */
	private final LinkedHashMap<String, ObjectFieldMetadata> fieldMetaById = new LinkedHashMap<String, ObjectFieldMetadata>();

	/**
	 * 
	 * @param raw
	 */
	public void process(RawObject raw) {
		for (ObjectField field : raw.getObjectFields()) {
			ObjectFieldMetadata fieldMeta = fieldMetaById.get(field.getId());
			if (fieldMeta == null) {
				fieldMeta = new ObjectFieldMetadata(field.getId());
				fieldMetaById.put(field.getId(), fieldMeta);
			}
			fieldMeta.updateStatistics(field);
		}
	}

	/**
	 * 
	 * @param raw
	 * @return ObjectFieldMetaData map keyed by ObjectField Identifier
	 */
	public LinkedHashMap<String, ObjectFieldMetadata> getObjectFields() {
		if (fieldMetaById == null) {
			populateFieldMeta();
		}
		return fieldMetaById;
	}

	protected void populateFieldMeta() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("This is a code stub.");
	}
}
