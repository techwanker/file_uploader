package com.insightpartners.objectmetadata;

public class ObjectTypeId {

	/**
	 * Unique identifier for this ObjectType.
	 */
	private final String id;

	/**
	 * Construct a new ObjectTypeId.
	 * 
	 * @param id
	 *            Unique identifier for an object type.
	 */
	public ObjectTypeId(String id) {
		super();
		this.id = id;
	}

	/**
	 * Accessor for id.
	 * 
	 * @return unique identifier for ObjectType.
	 */
	public String getId() {
		return id;
	}

}
