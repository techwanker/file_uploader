package com.insightpartners.objectmetadata;

import java.util.List;

public class RawObject {

	/**
	 * The raw Object.
	 */
	private final byte[] rawObject;

	/**
	 * Construct a new RawObject.
	 * 
	 * @param rawObject
	 */
	public RawObject(byte[] rawObject) {
		super();
		this.rawObject = rawObject;
	}

	/**
	 * Get the identifier for the object type.
	 * 
	 * @return
	 */
	public ObjectTypeId getId() {
		// TODO
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @return the raw object.
	 */
	public byte[] getRawObject() {
		return rawObject;
	}

	/**
	 * Parse out the ObjectField into a List sequenced as they are encountered.
	 * 
	 * @return
	 */
	public List<ObjectField> getObjectFields() {
		throw new UnsupportedOperationException("This is a code stub.");
	}

}
