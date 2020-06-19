package org.javautil.dex.bsh;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import bsh.NameSpace;
import bsh.UtilEvalError;

import org.javautil.dex.parser.UncheckedParseException;
//import org.javautil.jdbc.UncheckedSQLException;



public class ResultSetIterator implements Iterable<ResultSet>, Iterator<ResultSet> {
	private final ResultSet rset;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private boolean hasNextCalled = false;

	private boolean nextResult = true;

	private String recordName = null;

	private NameSpace nameSpace = null;

	private ResultSetMetaData meta = null;

	private final String newline = System.getProperty("line.separator");

	private boolean showResultSetValues  = false;
	private final boolean showMetaData  = true;
	int recordNumber = 0;

	public ResultSetIterator(final ResultSet rset, final NameSpace nameSpace,
			final String recordName) {
		if (rset == null) {
			throw new IllegalArgumentException("rset is null");
		}
		if (nameSpace == null) {
			throw new IllegalArgumentException("nameSpace is null");
		}
		this.rset = rset;
		this.nameSpace = nameSpace;
		this.recordName = recordName;
	}

	public boolean hasNext() {
		if (!hasNextCalled) {
			try {
				logger.debug("calling next");
				nextResult = rset.next();
				recordNumber++;
				if (nextResult) {
					populateNameSpace();
				}
			} catch (final SQLException e) {
				throw new RuntimeException(e);
			} catch (final UtilEvalError e) {
				throw new RuntimeException(e);
			}
			hasNextCalled = true;
		}
		if (!nextResult) {
			logger.info("exhausted resultset");
		}
		return nextResult;
	}

	public boolean isShowResultSetValues() {
		return showResultSetValues;
	}

	@SuppressWarnings("unchecked")
	public Iterator<ResultSet> iterator() {
		return this;
	}

	public ResultSet next() {

		try {
			if (hasNextCalled) {
				logger.debug("skipping call to next");
				hasNextCalled = false;
			} else {
				logger.debug("calling next");
				if (rset.next()) {
					recordNumber++;
					populateNameSpace();
				}
			}
		} catch (final SQLException sqe) {
			throw new RuntimeException(sqe);
		} catch (final UtilEvalError e) {
			throw new RuntimeException(e);
		}
		return rset;
	}

	/**
	 * Initializes the values from the metadata
	 * @throws SQLException
	 * @throws UtilEvalError
	 */
	public void populateNameSpaceFromMetaData() throws SQLException, UtilEvalError {
		if (meta == null) {
			meta = rset.getMetaData();
		}
		if (nameSpace == null) {
			logger.error("namespace is null");
			throw new IllegalStateException("nameSpace is null");
		}
		System.out.println("ResultSetIterator populateNameSpaceFromMetaData");
		message("column count " + meta.getColumnCount());
		for (int i = 1; i <= meta.getColumnCount(); i++) {

			// @todo quoted variable names will cause this to blow up
			final String columnName = meta.getColumnName(i);

//			String schema = meta.getSchemaName(i);
//			String table = meta.getTableName(i);
//			String dbObjectName = schema + "." + table + "."  + columnName;
			final String columnClassName = meta.getColumnClassName(i);
			final Object value = "Undefined";
			//logger.debug("setting '" + name + "' to " + value);

			final String qualifiedName = recordName + "$" + columnName;
			message("showMetaData " + showMetaData);
			//logger.debug(setMessage);
			if (showMetaData) {
				final String setMessage = "meta: " +  columnName + " qualifiedName: '" + qualifiedName + ": '" +  columnClassName;
				final Level oldLevel = logger.getLevel();
				logger.setLevel(Level.DEBUG);
				logger.debug(setMessage);
				logger.setLevel(oldLevel);
				System.out.println(setMessage);
			}
			nameSpace.setVariable(qualifiedName, value, false);
		}
	}

	/*
	 * TODO what was this
	 * (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
		try {
			rset.deleteRow();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}

	}

	public void setShowResultSetValues(final boolean showResultSetValues) {
		this.showResultSetValues = showResultSetValues;
	}

	public void showResultSetValues() {
		setShowResultSetValues(true);
	}

	private void message(final String text) {
		final String ID= "ResultSetIterator";
		System.out.println(ID + " " + text);
	}

	private void populateNameSpace() throws SQLException, UtilEvalError {
		if (meta == null) {
			meta = rset.getMetaData();
		}

		for (int i = 1; i <= meta.getColumnCount(); i++) {

			// @todo quoted variable names will cause this to blow up
			final String name = meta.getColumnName(i);
			final Object value = rset.getObject(i);
			logger.debug("setting '" + name + "' to " + value);
			if (nameSpace == null) {
				logger.error("namespace is null");
				throw new IllegalStateException("nameSpace is null");
			}
			final String qualifiedName = recordName + "$" + name;

			//logger.debug(setMessage);
			if (showResultSetValues || logger.isDebugEnabled()) {
				final String setMessage = "record # " + recordNumber +  " qualifiedName: '" + qualifiedName + ": '" + value.toString() + "' " + value.getClass().getName();
				final Level oldLevel = logger.getLevel();
				logger.setLevel(Level.DEBUG);
				logger.debug(setMessage);
				logger.setLevel(oldLevel);
				System.out.println(setMessage);
			}
			nameSpace.setVariable(qualifiedName, value, false);
		}
	}
}
