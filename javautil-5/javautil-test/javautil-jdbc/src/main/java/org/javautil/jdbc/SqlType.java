package org.javautil.jdbc;

import java.sql.Types;

public enum SqlType {
	ARRAY, BIGINT, BINARY, BIT, BLOB, CHAR, CLOB, DATE, DECIMAL, DISTINCT, DOUBLE, FLOAT, INTEGER, JAVA_OBJECT, LONGVARBINARY, LONGVARCHAR, NULL, NUMERIC, OTHER, REAL, REF, SMALLINT, STRUCT, TIME, TIMESTAMP, TINYINT, VARBINARY, VARCHAR;

	public static SqlType getInstance(final int type) {
		SqlType retval = null;
		switch (type) {
		case Types.ARRAY:
			retval = ARRAY;
			break;
		case Types.BIGINT:
			retval = BIGINT;
			break;
		case Types.BINARY:
			retval = BINARY;
			break;
		case Types.BIT:
			retval = BIT;
			break;
		case Types.BLOB:
			retval = BLOB;
			break;
		case Types.CHAR:
			retval = CHAR;
			break;
		case Types.CLOB:
			retval = CLOB;
			break;
		case Types.DATE:
			retval = DATE;
			break;
		case Types.DECIMAL:
			retval = DECIMAL;
			break;
		case Types.DISTINCT:
			retval = DISTINCT;
			break;
		case Types.DOUBLE:
			retval = DOUBLE;
			break;
		case Types.FLOAT:
			retval = FLOAT;
			break;
		case Types.INTEGER:
			retval = INTEGER;
			break;
		case Types.JAVA_OBJECT:
			retval = JAVA_OBJECT;
			break;
		case Types.LONGVARBINARY:
			retval = LONGVARBINARY;
			break;
		case Types.LONGVARCHAR:
			retval = LONGVARCHAR;
			break;
		case Types.NULL:
			retval = NULL;
			break;
		case Types.NUMERIC:
			retval = NUMERIC;
			break;
		case Types.OTHER:
			retval = OTHER;
			break;
		case Types.REAL:
			retval = REAL;
			break;
		case Types.REF:
			retval = REF;
			break;
		case Types.SMALLINT:
			retval = SMALLINT;
			break;
		case Types.STRUCT:
			retval = STRUCT;
			break;
		case Types.TIME:
			retval = TIME;
			break;
		case Types.TIMESTAMP:
			retval = TIMESTAMP;
			break;
		case Types.TINYINT:
			retval = TINYINT;
			break;
		case Types.VARBINARY:
			retval = VARBINARY;
			break;
		case Types.VARCHAR:
			retval = VARCHAR;
			break;
		default:
			throw new IllegalArgumentException("unknown sqlType " + type);
		}
		return retval;
	}
}
