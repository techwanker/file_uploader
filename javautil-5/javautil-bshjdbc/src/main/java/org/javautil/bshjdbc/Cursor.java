package org.javautil.bshjdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.javautil.persistence.PersistenceException;
import org.javautil.sql.QueryHelper;

import bsh.NameSpace;

public class Cursor implements Iterable<ResultSet> {

	private final Connection conn;
	private final String cursorName;
	private final String sqlText;

	private ResultSetMetaData meta;

	private QueryHelper stmtHelper;

	private final NameSpace nameSpace;

	public Cursor(final Connection conn, final String cursorName,
			final String sqlText, final NameSpace nameSpace)
			throws PersistenceException {

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
	}

	@Override
	@SuppressWarnings("unchecked")
	public ResultSetIterator iterator() {
		ResultSet rset = null;
		try {
			stmtHelper = new QueryHelper(conn, sqlText);
			Map<String, Object> binds = NamespaceHelper.getVariables(nameSpace);
			stmtHelper.setBinds(binds);
			rset = stmtHelper.executeQuery();
			meta = rset.getMetaData();
		} catch (final SQLException e) {
			throw new PersistenceException(sqlText, e);
		}
		ResultSetIterator iterator = new ResultSetIterator(rset, nameSpace,
				cursorName);
		return iterator;
	}

	public String getCursorName() {
		return cursorName;
	}

	public ResultSetMetaData getMetaData() {
		return meta;
	}

	public String getSqlText() {
		return sqlText;
	}
}
