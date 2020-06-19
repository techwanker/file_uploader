package org.javautil.dex;

import java.sql.Types;

import javax.activation.UnsupportedDataTypeException;

public class XmlTypeHelper {
    /**
     * Oracle Datatype for number with scale = 0 and precision < 10.
     */
    public static final int INT       = 1;

    /**
     * Oracle Datatype is varchar2 or varchar or char.
     */
    public static final int STRING    = 2;

    /**
     *  Oracle Datatype for number with scale = 0 and precision > 9 and < 19
     */
    public static final int LONG      = 3;

    /**
     * Oracle Datatype for number with scale != 0
     */
    public static final int DOUBLE    = 4;

    /**
     * Oracle Datatype for Date.
     */
    public static final int DATE      = 5;

    /**
     *          Oracle Datatype for Character Large Character Object.
     */

    public static final int CLOB      = 6;

    /**
     * Oracle Datatype is raw data.
     */
    public static final int VARBINARY = 7;

    /**
     * Oracle Datatype for large numbers
     */
    public static final int NUMBER    = 8;

    public static int getNumberType(final int columnSize, final int decimalDigits) {
        int returnValue;

        if (columnSize == 0 && decimalDigits == 0 || columnSize == 22) {
            returnValue = NUMBER;
        } else if (decimalDigits != 0) {
            returnValue = DOUBLE;
        } else {
            if (columnSize < 10) {
                returnValue = INT;
            } else {
                if (columnSize < 19) {
                    returnValue = LONG;
                } else {
                    returnValue = NUMBER;
                }
            }
        }

        return returnValue;

    }

    public static String getXmlType(final String tableName, final String columnName, final String typeName, final short dataType, final int columnSize,
            final int decimalDigits) throws UnsupportedDataTypeException {
        String returnValue = null;

        if (typeName.equals("LONG")) {
            returnValue = "long-string";
        }
        if (typeName.equals("NUMBER")) {

            final int type = getNumberType(columnSize, decimalDigits);
            switch (type) {
            case INT:
            case LONG:
            case NUMBER:
                returnValue = "number-" + columnSize;
                break;
            case DOUBLE:
                returnValue = "number-" + columnSize + "-" + decimalDigits;
                break;
            }

        } else {
            switch (dataType) {
            case Types.CHAR:
                returnValue = "char-" + columnSize;
                break;
            case Types.VARCHAR:
                returnValue = "varchar2-" + columnSize;
                break;
            case Types.TIMESTAMP:
                returnValue = "timestamp";
                break;
            case Types.DATE:
                returnValue = "date";
                break;
            case Types.LONGVARCHAR:
                returnValue = "long-string";
                break;
            case Types.CLOB:
                returnValue = "clob";
                break;
            case Types.VARBINARY:
                returnValue = "varbinary";
                break;
            default:
                throw new UnsupportedDataTypeException("unknown type " + dataType + " for column " + columnName + " in "
                        + tableName);
            }
        }
        if (returnValue == null) {
            throw new java.lang.IllegalArgumentException("unknown type type: " + dataType + " for column " + columnName);
        }

        return returnValue;
    }

}
