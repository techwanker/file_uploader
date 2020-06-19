package com.insightpartners.objectmetadata;

public class ObjectFieldMetadata {

	/**
	 * Identifier for field.
	 */
	private final String id;
	/**
	 * Identifier for the object in which this can be found.
	 */
	private String containingObjectId;
	/**
	 * Number of occurrences of this field.
	 */
	private int instanceCount;

	/**
	 * Number of fields that were null or zero length strings.
	 */
	private int nullCount;

	/**
	 * Number of fields that were numbers.
	 */
	private int numberCount;

	/**
	 * Maximum number of digits left of decimal point.
	 */
	private int maximumIntegerDigits;

	/**
	 * Maximum number of digits found after decimal point.
	 */
	private int maximumFractionalDigits;

	/**
	 * Number of occurrences that could be coerced into a date.
	 */
	private int dateCount;

	/**
	 * Construct a new ObjectFieldMetaData
	 * 
	 * @param id
	 *            unique identifer for this ObjectField (not this instance).
	 */
	public ObjectFieldMetadata(String id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}
		this.id = id;
	}

	/**
	 * Update statistics for this ObjectField.
	 * 
	 * @param field
	 */
	public void updateStatistics(ObjectField field) {
		throw new UnsupportedOperationException("This is a code stub.");
	}

	/**
	 * Get the ObjectField unique identifier.
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Return the identifier for the the ObjectType that contains this field.
	 * 
	 * @return
	 */
	public String getContainingObjectId() {
		return containingObjectId;
	}

	/**
	 * Number of occurrences of this ObjectField in the database.
	 * 
	 * @return
	 */
	public int getInstanceCount() {
		return instanceCount;
	}

	/**
	 * Number of occurrence of a null or zero length string in this field in the
	 * database.
	 * 
	 * @return
	 */
	public int getNullCount() {
		return nullCount;
	}

	public int getNumberCount() {
		return numberCount;
	}

	public int getMaximumIntegerDigits() {
		return maximumIntegerDigits;
	}

	public int getMaximumFractionalDigits() {
		return maximumFractionalDigits;
	}

	public int getDateCount() {
		return dateCount;
	}

}
