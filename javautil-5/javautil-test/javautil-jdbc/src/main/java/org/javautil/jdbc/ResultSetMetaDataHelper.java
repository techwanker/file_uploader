package org.javautil.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.javautil.jdbc.metadata.renderer.XmlMeta;

/**
 * to do, create FileHelper
 */
public class ResultSetMetaDataHelper {
	// @todo externalize element name
	public static Element asElement(ResultSetMetaData meta) throws SQLException {
		if (meta == null) {
			throw new IllegalArgumentException("meta is null");
		}
		Element el = DocumentHelper.createElement("metaData");
		int columnCount = meta.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			String columnName = meta.getColumnName(i);
			String columnTypeName = meta.getColumnTypeName(i);
			int precision = meta.getPrecision(i);
			int scale = meta.getScale(i);
			Element column = el.addElement("column");
			column.addAttribute(XmlMeta.COLUMN_NAME, columnName);
			column.addAttribute(XmlMeta.DATA_TYPE, columnTypeName);
			column.addAttribute(XmlMeta.PRECISION, Integer.toString(precision));
			column.addAttribute(XmlMeta.SCALE, Integer.toString(scale));
		}

		return el;
	}

	// public ResultSetMetaDataHelper(ResultSet rset) {
	// }

	/**
	 * for use in service request, throwing an exception is pointless
	 * 
	 * @param conn
	 * @param sqlText
	 * @return
	 */
	public static String getMetaDataAsHtml(Connection conn, String sqlText) {
		StringBuilder buff = new StringBuilder();
		PreparedStatement stmt = null;
		ResultSetMetaData meta;
		try {
			stmt = conn.prepareStatement(sqlText);

			stmt.close();

			meta = stmt.getMetaData();
			int columnCount = meta.getColumnCount();
			buff.append("<table>");
			for (int column = 1; column <= columnCount; column++) {

				buff.append("<tr>");
				buff.append("<th>");
				buff.append(meta.getColumnLabel(column));
				buff.append("</th>");
				buff.append("<th>");
				meta.getColumnLabel(column);
				buff.append("</th>");
				meta.getColumnTypeName(column);
				buff.append("</tr>\n");
			}
			buff.append("</table>");
			return buff.toString();
		} catch (SQLException e) {
			return e.getMessage();
		}
	}

	public static String metaDataToHtml(ResultSetMetaData meta) {
		StringBuffer buff = new StringBuffer(2048);
		int columnCount;
		buff.append("<tr>");
		try {
			columnCount = meta.getColumnCount();
			for (int column = 1; column <= columnCount; column++) {
				buff.append("<th>");
				buff.append(meta.getColumnLabel(column));
				buff.append("</th>");
			}
		} catch (java.sql.SQLException s) {
			throw new java.lang.IllegalStateException(
					"JDBC driver is defective, reported column count not returned on ResultSetMetaData");
		}
		buff.append("</tr>\n");
		return new String(buff);
	}

	public static String metaDataToString(ResultSetMetaData meta)
			throws java.sql.SQLException {
		StringBuffer buff = new StringBuffer(2048);
		int columnCount = meta.getColumnCount();

		for (int column = 1; column <= columnCount; column++) {
			buff.append("\"");
			buff.append(meta.getColumnLabel(column));
			buff.append("\"");
			if (column < columnCount) {
				buff.append(",");
			}
		}
		return new String(buff);
	}

	/**
	 * non instantiable.
	 * 
	 */
	private ResultSetMetaDataHelper() {
	}
}
