package com.insightpartners.objectmetadata;

public class ObjectField {

	/**
	 * Identifier for the field.
	 */
	private final String id;

	/**
	 * Construct a new object field.
	 * 
	 * @param id
	 *            Unique identifier for the ObjectField.
	 */
	public ObjectField(String id) {
		super();
		this.id = id;
	}

	/**
	 * Get the object field identifier.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

}
