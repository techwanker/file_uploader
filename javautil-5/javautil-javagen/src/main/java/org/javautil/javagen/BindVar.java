package org.javautil.javagen;

/**
 * @todo what is this?
 */
class BindVar {
	/** As encountered in select statement - start with 0 - not used in hash */
	int seq = 0; // As encountered in select statement - start with 0 - not used
					// in hash
	/** bind variable to use in the select for parsing **/
	String varName = "";
	/**
	 * INT, STR, LONG, DOUBLE
	 */
	int type = 0;
	/** The actual java type (int, double, String, etc) */
	String javaType = "";
	/**
	 * Value to substitute in the select statement (1 for a NUmber, 'X' for a
	 * Varchar2, etc)
	 */
	String substituteVal = "";

	@Override
	public String toString() {
		return ("seq: " + seq + " varName: " + varName + " type: " + type
				+ " javaType: " + javaType);
	}
}
