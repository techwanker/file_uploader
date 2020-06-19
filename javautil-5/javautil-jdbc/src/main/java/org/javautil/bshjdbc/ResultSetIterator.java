package org.javautil.bshjdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.NameSpace;
import bsh.UtilEvalError;


/**
 * Iterates over a ResultSet.
 * 
 * TODO what closes the ResultSet
 * @author jjs
 *
 */



public class ResultSetIterator implements Iterable<ResultSet>, Iterator<ResultSet> {
	private final ResultSet rset;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Set to false in the "next" call. Set to true in hasNext();
	 * 
	 * 
	 */
	private boolean hasNextCalled = false;

	private boolean nextResult = true;

	private String cursorName = null;

	private NameSpace nameSpace = null;

	private ResultSetMetaData meta = null;

	private final String newline = System.getProperty("line.separator");

	private boolean showResultSetValues  = false;
	private final boolean showMetaData  = true;
	private int rowIndex = 0;

	/**
	 * 
	 * 
	 * @param rset
	 * @param nameSpace
	 * @param cursorName
	 */
	public ResultSetIterator(final ResultSet rset, final NameSpace nameSpace,
			final String cursorName) {
		if (rset == null) {
			throw new IllegalArgumentException("rset is null");
		}
		if (nameSpace == null) {
			throw new IllegalArgumentException("nameSpace is null");
		}
		if (cursorName == null) {
			throw new IllegalArgumentException("cursorName is null");
		}
		this.rset = rset;
		this.nameSpace = nameSpace;
		this.cursorName = cursorName;
	}

	/**
	 * 
	 */
	public boolean hasNext() {
		if (!hasNextCalled) {
			try {
				logger.debug("calling next");
				nextResult = rset.next();
				rowIndex++;
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
				logger.trace("calling resultset next");
				if (rset.next()) {
					rowIndex++;
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
		
			throw new IllegalStateException("nameSpace is null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("ResultSetIterator populateNameSpaceFromMetaData");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("column count " + meta.getColumnCount());
		}
		for (int i = 1; i <= meta.getColumnCount(); i++) {

			// @todo quoted variable names will cause this to blow up
			final String columnName = meta.getColumnName(i);
			final String columnClassName = meta.getColumnClassName(i);
			final Object value = "Undefined";
			final String qualifiedName = cursorName + "$" + columnName;


			if (showMetaData) {
				final String setMessage = "meta: " +  columnName + " qualifiedName: '" + qualifiedName + ": '" +  columnClassName;
//				final Level oldLevel = logger.getLevel();
//		
//				logger.debug(setMessage);
//				logger.setLevel(oldLevel);
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

//	private void message(final String text) {
//		final String ID= "ResultSetIterator";
//		System.out.println(ID + " " + text);
//	}

	private void populateNameSpace() throws SQLException, UtilEvalError {
		if (meta == null) {
			meta = rset.getMetaData();
		}
		if (nameSpace == null) {
			throw new IllegalStateException("nameSpace is null");
		}
		for (int i = 1; i <= meta.getColumnCount(); i++) {

			// TODO quoted variable names will cause this to blow up
			final String name = meta.getColumnName(i);
			final Object value = rset.getObject(i);
			logger.debug("setting '" + name + "' to " + value);
			
			final String qualifiedName = cursorName + "$" + name;

			if (showResultSetValues || logger.isTraceEnabled()) {
				final String setMessage = "record # " + rowIndex +  " qualifiedName: '" + qualifiedName + ": '" + value.toString() + "' " + value.getClass().getName();
			//	final Level oldLevel = logger.getLevel();
	
				logger.trace(setMessage);
			//	logger.setLevel(oldLevel);

			}
			nameSpace.setVariable(qualifiedName, value, false);
		}
	}
	
	
}
