package org.javautil.dex.bsh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import bsh.NameSpace;

import org.javautil.dex.dexterous.jdbc.SqlStatement;
import org.javautil.persistence.PersistenceException;


public class Cursor  implements Iterable<ResultSet>{
	private static final String newline = System.getProperty("line.separator");
	private final Connection conn;
	private final String cursorName;
	private final String sqlText;

	private ResultSet rset;
	private ResultSetMetaData meta;
	private PreparedStatement statement;

	private SqlStatement stmtHelper;
	private ResultSetIterator iterator;
	private final NameSpace nameSpace;
	private String recordName;

	public Cursor (final Connection conn, final String cursorName, final String sqlText, final NameSpace nameSpace, final String recordName)  throws PersistenceException {

		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		if (cursorName == null) {
			throw new IllegalArgumentException("cursorName is null");
		}
		if (sqlText == null) {
			throw new IllegalArgumentException("sqlText is null");
		}
		if (nameSpace == null) {
			throw new IllegalArgumentException("namespace is null");
		}
		this.conn = conn;
		this.cursorName = cursorName;
		this.sqlText = sqlText;
		this.nameSpace = nameSpace;
		this.recordName = recordName;
		init();
	}

	public String getCursorName() {
		return cursorName;
	}

	public ResultSetMetaData getMetaData() {
		return meta;
	}


	public String getRecordName() {
		return recordName;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void init() throws  PersistenceException {
		if (conn == null) {
			throw new IllegalStateException("conn is null");
		}
		if (sqlText == null) {
			throw new IllegalStateException("sqlText is null");
		}
		try {
		statement = conn.prepareStatement(sqlText);

		} catch (final SQLException sqe) {

			throw new PersistenceException(sqlText,sqe);
//			String msg = "while processing " + newline + sqlText + newline + sqe.getMessage();
//			sqe.printStackTrace();
//			throw sqe;
		}

	}

	@SuppressWarnings("unchecked")
	public ResultSetIterator iterator() {
		try {
			stmtHelper = new SqlStatement(sqlText);
			stmtHelper.bind(statement, nameSpace);
			rset = statement.executeQuery();
			meta = rset.getMetaData();
		} catch (final SQLException e) {
			throw new UncheckedSQLException(e);
		}
		iterator = new ResultSetIterator(rset,nameSpace,recordName);
		return iterator;
	}

	public void setRecordName(final String recordName) {
		this.recordName = recordName;

	}
}
