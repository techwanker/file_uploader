package org.javautil.dex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

// todo how many variants of this have I written and how many places are they scattered in
// todo use the xml meta data classes and transform using xslt ???
public class JdbcHelper {
	private static String newline = System.getProperty("line.separator");

	public static String describe(final Connection conn, final String dbObjectName) throws SQLException {
		final StringBuilder b = new StringBuilder();
		if (dbObjectName == null) {
			throw new IllegalArgumentException("describe " + " requires table or view name");
		}

		final PreparedStatement stmt = conn.prepareStatement("select * from " + dbObjectName + " where 1 = 2");
		final ResultSet rset = stmt.executeQuery();
		final ResultSetMetaData meta = rset.getMetaData();
		for (int i = 1; i < meta.getColumnCount(); i++) {
			final String columnName = meta.getColumnName(i);
			b.append("columnName: " + columnName + " ");
			final int sqlType = meta.getColumnType(i);
			b.append("sqlType: " + sqlType + " ");
			final String typeName = meta.getColumnTypeName(i);
			b.append("typeName: " + typeName + " ");
			final int precision = meta.getPrecision(i);
			b.append("precision: " + precision + " ");
			final int scale = meta.getScale(i);
			b.append("scale: " + scale + " ");
			// class objectClass = meta.getClass();
			// boolean isAutoIncrement = meta.isAutoIncrement(i);
			// boolean isCaseSensitive = meta.isCaseSensitive(i);
			// boolean isCurrency = meta.isCurrency(i);
			// int isNullable = meta.isNullable(i);
			// boolean isSearchable = meta.isSearchable(i);
			// boolean isSigned = meta.isSigned(i);
			b.append("\n");
		}
		return b.toString();
	}

	public static void generateJdbcSelect(final Connection conn, final String tableName) throws SQLException {

		final StringBuilder b = new StringBuilder();
		if (tableName == null) {
			throw new IllegalArgumentException("generate select  " + " requires table or view name");
		}
		final PreparedStatement stmt = conn.prepareStatement("select * from " + tableName + " where 1 = 2");
		final ResultSet rset = stmt.executeQuery();
		final ResultSetMetaData mdata = rset.getMetaData();
		final StringBuffer stmtText = new StringBuffer();
		stmtText.append("\"\" +\n");
		stmtText.append("         \"SELECT\\n\" +\n");
		// now the columns
		for (int colNum = 1; colNum <= mdata.getColumnCount(); colNum++) {
			final String label = mdata.getColumnLabel(colNum).toLowerCase();
			stmtText.append("        \"    " + label);
			if (colNum < mdata.getColumnCount()) {
				stmtText.append(",");
			}
			stmtText.append("\\n\" +\n");
		}
		stmtText.append("        \"FROM " + tableName + "\\n\";\n\n");
		stmt.close();
		//
		final String stmtString = stmtText.toString();
		// @todo check that output mime type is valid for operation
		// @todo write to writers
		System.out.println(stmtString);

	}

	public static void generateSelect(final Connection conn, final String tableName) throws SQLException {
		final StringBuilder b = new StringBuilder();
		if (tableName == null) {
			throw new IllegalArgumentException("generate select  " + " requires table or view name");
		}
		final PreparedStatement stmt = conn.prepareStatement("select * from " + tableName + " where 1 = 2");
		final ResultSet rset = stmt.executeQuery();
		final ResultSetMetaData mdata = rset.getMetaData();
		final StringBuffer stmtText = new StringBuffer();

		stmtText.append("SELECT" + newline);
		// now the columns
		for (int colNum = 1; colNum <= mdata.getColumnCount(); colNum++) {
			final String label = mdata.getColumnLabel(colNum).toLowerCase();
			stmtText.append("       " + label);
			if (colNum < mdata.getColumnCount()) {
				stmtText.append(",");
			}
			stmtText.append(newline);
		}
		stmtText.append("FROM " + tableName + newline);
		stmt.close();
		//
		final String stmtString = stmtText.toString();
		// @todo check that output mime type is valid for operation
		// @todo write to writers
		System.out.println(stmtString);

	}

}
