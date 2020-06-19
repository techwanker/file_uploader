/*
 
 */
package org.javautil.jdbc.resultset;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Usage: instantiate
 * 
 * optionally set format String for dates.
 */
public class JdbcResultSetHelper {

	public static final String revision = "$Revision: ";

	//
	private static HashMap<Integer, Integer> smallIntegers = new HashMap<Integer, Integer>();

	private final Logger logger = Logger.getLogger(getClass());
	/** @todo propagate */
	@SuppressWarnings("unused")
	private String dateFormat;

	private final ResultSet rset;

	private final ResultSetMetaData meta;

	private ArrayList<Object[]> rows;

	private long rowCount = 0;

	private SimpleDateFormat dateFormatter = null;

	private final HashMap<String, ResultSetFormatRule> formatMap = new HashMap<String, ResultSetFormatRule>();

	// private StringStores stringStores = new StringStores();

	/**
	 * numbers 1 to 255
	 */

	// meta Data
	HashMap<String, Integer> columnNameIndex = new HashMap<String, Integer>();

	static {
		for (int i = 0; i <= 256; i++) {
			Integer val = new Integer(i);
			smallIntegers.put(val, val);
		}
	}

	public JdbcResultSetHelper(ResultSet rset) throws SQLException {
		if (rset == null) {
			throw new IllegalArgumentException("rset is null");
		}
		this.rset = rset;
		meta = rset.getMetaData();
		for (int i = 1; i < meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			columnNameIndex.put(columnName, getInteger(i));
		}
		// meta.
	}

	public JdbcResultSetHelper(ResultSet rset,
			ArrayList<ResultSetFormatRule> formatRules) throws SQLException {
		this.rset = rset;
		// this.formatRules = formatRules;
		meta = rset.getMetaData();
		for (int i = 1; i < meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			columnNameIndex.put(columnName, getInteger(i));
		}
		for (ResultSetFormatRule rule : formatRules) {
			formatMap.put(rule.getName(), rule);
		}
		// System.
		// meta.
	}

	public void fetch() throws SQLException {
		rows = new ArrayList<Object[]>();
		while (rset.next()) {
			Object[] columns = new Object[meta.getColumnCount()];
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				columns[i] = rset.getObject(i);
			}
			rows.add(columns);
		}
	}

	public String getMetaDataAsCsv() throws SQLException {
		StringBuilder buff = new StringBuilder();
		for (int i = 1; i <= meta.getColumnCount(); i++) {
			String columnName = meta.getColumnName(i);
			buff.append("\"");
			buff.append(columnName);
			buff.append("\"");
			if (i < meta.getColumnCount()) {
				buff.append(",");
			}
		}
		return buff.toString();
	}

	public String getNextAsCsv() throws SQLException {
		StringBuilder buff = new StringBuilder();

		for (int column = 1; column <= meta.getColumnCount(); column++) {
			// @todo shouldn't get as string if we have to refetch
			String data = rset.getString(column);
			if (data != null) {
				switch (meta.getColumnType(column)) {

				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case Types.CHAR:
					buff.append("\"");
					buff.append(data.replaceAll("\"", "\"\""));
					buff.append("\"");
					break;
				case Types.DATE:
					if (dateFormatter != null) {
						Date d = rset.getDate(column);
						String fmt = dateFormatter.format(d);
						buff.append(fmt);
					} else {
						data = rset.getDate(column).toString();
						buff.append(data);
					}
					break;
				case Types.TIMESTAMP: // get as java.sql.TimeStamp not
										// oracle.sql.Timestamp
					Timestamp t = rset.getTimestamp(column);
					data = t.toString().substring(0, 16);
					buff.append(data);
					break;
				case Types.NUMERIC:
				case Types.DECIMAL:
				case Types.DOUBLE:
				case Types.FLOAT:
				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					buff.append(data);
					break;
				default:
					logger.warn("unsupported type for column "
							+ meta.getColumnName(column));
				}
				if (column < meta.getColumnCount()) {
					buff.append(",");
				}
			} else {
				if (column < meta.getColumnCount()) {
					buff.append(",");
				}
			}
		}
		return buff.toString();
	}

	public long getRowcount() {
		return rowCount;
	}

	/**
	 * 
	 * @param rowIndex
	 *            - relative zero, the first is zero
	 * @param columnIndex
	 *            - relative 1 to be consistent with jdbc
	 * @return
	 */
	public String getString(int rowIndex, int columnIndex) {
		Object[] row;
		Object data;
		String returnValue = null;
		try {
			row = rows.get(rowIndex);
		} catch (IndexOutOfBoundsException r) {
			throw new IndexOutOfBoundsException("rows contains " + rows.size()
					+ "; cannot get " + rowIndex);
		}
		try {
			data = row[columnIndex - 1];
		} catch (IndexOutOfBoundsException c) {
			throw new IndexOutOfBoundsException("there are " + row.length
					+ " columns cannot get " + columnIndex
					+ " valid values are 1 to " + row.length);
		}
		if (data != null) {
			returnValue = data.toString();
		}
		return returnValue;

	}

	public String getString(int rowIndex, String columnName) {
		Integer columnIndex = columnNameIndex.get(columnName);
		if (columnIndex == null) {
			throw new IllegalArgumentException("no such column as "
					+ columnName);
		}
		return getString(rowIndex, columnIndex.intValue());
	}

	public boolean next() throws SQLException {
		rowCount++;
		return rset.next();
	}

	// public NodeList getAsNodeList() throws ParserConfigurationException {
	// StringBuilder buff = new StringBuilder();
	// NodeSet resultSet = new NodeSet();
	// Document doc =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
	// for (Date d : dateBuckets) {
	// Element element = doc.createElement("bucket");
	// String bucket = outputFormatter.format(d);
	// element.appendChild((doc.createTextNode(bucket)));
	// resultSet.addNode(element);
	// }
	// //System.out.println("emitting buckets");
	// // for (String b : buckets) {
	// // System.out.println("bucket==> " + b);
	// // }
	// return resultSet;
	//
	// }

	/**
	 * Sets the date formatter.
	 * 
	 * Nulls out the formatter if input is null
	 * 
	 * @param dateFormat
	 */
	public void setDateFormat(String dateFormat) {
		if (dateFormat != null) {
			this.dateFormat = dateFormat;
			dateFormatter = new SimpleDateFormat(dateFormat);
		} else {
			dateFormatter = null;
		}

	}

	private Integer getInteger(int value) {
		Integer returnValue = smallIntegers.get(value);
		if (returnValue == null) {
			returnValue = new Integer(value);
		}
		return returnValue;
	}

	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	public void setDateFormatter(SimpleDateFormat dateFormatter) {
		this.dateFormatter = dateFormatter;
	}
}
