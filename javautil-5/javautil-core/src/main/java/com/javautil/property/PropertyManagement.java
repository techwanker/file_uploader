package com.javautil.property;

public interface PropertyManagement {

	public abstract boolean getBooleanProperty(String key, boolean dflt);

	public abstract boolean getBooleanPropertyNoWarn(String key, boolean dflt);

	public abstract String getMandatoryProperty(String key)
			throws java.lang.IllegalStateException;

	public abstract String getProperty(String key);

	/**
	 *
	 * @param key
	 * @return Associated property.
	 * @deprecated should not use netProperties, usage is not discouraged, this is a developement note
	 */
	public abstract String getProperty(String key, boolean warn);

	public abstract String getProperty(String key, String dflt);

	/**
	 * Get the property names sorted by key.
	 *
	 */

	public abstract String[] getPropertyNames();

	public abstract String getPropertyNoWarn(String key);

	/**
	 * Returns true if the key exists and the value is true.
	 *
	 * @param key
	 *
	 * @see   #getBooleanProperty(String key, boolean dflt)
	 */

	public abstract boolean isTrue(String key);

}