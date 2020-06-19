/*
 
 */
package org.javautil.jdbc;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.javautil.persistence.PersistenceException;
import org.javautil.text.StringHelper;
import org.javautil.util.DateGenerator;
import org.javautil.util.EventHelper;

/**
 * @todo replace all cr/lf cr and lf to ' ' before parsing bind
 * @todo trim trailing ";" if present
 * @todo make sure that not too many binds are called before
 * @todo make sure := is not found as a bind
 * 
 * @since November 2005 Comments:
 *        <ul>
 *        <li>Supports auto-unboxing for numeric bind variables, so half the
 *        number of bind call overloads.</li>
 * 
 *        </ul>
 *        Constraints:
 *        <ul>
 *        Null bind variables are not supported. Not an often used construct
 *        </ul>
 *        Features:
 *        <ul>
 *        <li>bind variables can be specified by name</li>
 *        <li>the same name can be used repeatedly in a statement but only has
 *        to be specified as a bind variable once.
 *        <li>
 * 
 *        <ul>
 *        Example of usage: <code>
 * 
 * SelectHelper helper = new SelectHelper(conn,"FC_FCST",
 * " where fc_fcst_nbr = :pk");
 * helper.bind("pk",1);
 * FcFcst nfc = new FcFcst();
 * helper.getNextRow(nfc);
 * 
 * @todo finish off dateBucket logic </code>
 */
public class SelectHelper {
	public static final String revision = "$Revision: 1.1 $";

	/**
	 * Consumes anything that doesn't start with ' " : into group 1 group 2 will
	 * be one of ' " : group 3 will be everything after group 2
	 */
	public final static Pattern tokenFinder = Pattern
			.compile("([^\"':]*)(['\":])(.*)");

	/**
	 * Consumes anything that doesn't start with ' " : - into group 1 group 2
	 * will be one of ' " : -- : group 3 will be everything after group 2
	 */
	public final static Pattern tokenFinder2 = Pattern
			.compile("([^\"':-]*)(['\":]|--)(.*)");
	// public final static Pattern bindFinder =
	// Pattern.compile("([a-zA-Z$#_]*)(.*)");
	public final static Pattern bindNameFinder = Pattern
			.compile("([a-zA-Z$#_]*)(.*)");

	public final static Pattern quoteEndFinder = Pattern
			.compile("([^']*')(.*)");

	/**
	 * Consumes any number for characters that are not quotes and the trailing
	 * quote into group 1 and everything else into group 2
	 */
	public final static Pattern doubleQuoteEndFinder = Pattern
			.compile("([^\"*]\")(.*)");

	private static final Integer RSET_AVAILABLE = 200;

	private final boolean debug = false;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private final Level logLevel = Level.INFO;

	private final boolean camelCase = true;

	private HashMap<String, ArrayList<Integer>> bindVariables = new HashMap<String, ArrayList<Integer>>();

	private final HashMap<String, BindVariableValue> bindVariableValues = new HashMap<String, BindVariableValue>();

	private HashMap<String, String> camelMap;

	private HashMap<String, String> invertedCamel;

	private final ArrayList<String> columnLabels = new ArrayList<String>();

	private final HashSet<String> columnNames = new HashSet<String>();

	private int bindVariableCount = 0;

	private ResultSet rset;

	private PreparedStatement stmt;

	private String selectText;

	private boolean allowUnbound = false;
	private String dateBucketSourceColumnName;

	private String dateBucketDestinationColumnName;

	private boolean verbose = false;

	private final EventHelper events = new EventHelper();

	private final Integer SHOW_QUERY_TIMEOUT = 100;

	private boolean executingQuery = false;

	private Integer maxQuerySec = null;

	private long rowCount = -1;

	private final boolean logBindVariablesNotInSelect = false;

	private final boolean logBinds = false;

	private boolean ignoreUnnecessaryBinds = false;

	private long executeQueryMillis;

	public static String getRevision() {
		return revision;
	}

	public SelectHelper(Connection conn, String selectText) throws SQLException {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		if (selectText == null) {
			throw new IllegalArgumentException("selectText is null");
		}
		this.selectText = selectText;
		parseBindVariables();
		stmt = conn.prepareStatement(selectText);
	}

	public SelectHelper(Connection conn, String tableName, String whereClause)
			throws PersistenceException {
		try {
			selectText = "select * from " + tableName + " " + whereClause;
			parseBindVariables();
			stmt = conn.prepareStatement(selectText);
		} catch (SQLException s) {
			throw new PersistenceException("while processing " + selectText
					+ " " + s.getMessage(), s);
		}
	}

	public void addXmlElements(String wrapperElementName,
			String rowElementName, boolean asElements, Element root)
			throws SQLException {
		// check arguments
		if (root == null) {
			throw new IllegalArgumentException("root is null");
		}
		Element rsetElement = root.addElement(wrapperElementName);
		ResultSet rset = getResultSet();
		getColumnLabels(rset);
		addRows(rset, rsetElement, asElements, rowElementName);
		stmt.close();
		// return returnValue;
	}

	public void bind(String bindVariableName, BigDecimal value)
			throws SQLException {
		if (rset != null) {
			rset.close();
			rset = null;
		}
		ArrayList<Integer> bindNumbers = bindVariables.get(bindVariableName
				.toUpperCase());
		if (bindNumbers == null) {
			illegalBindVariable(bindVariableName);
		} else {
			for (Integer bindNumber : bindNumbers) {
				logger.info("binding '" + bindVariableName + "' index "
						+ bindNumber);
				stmt.setBigDecimal(bindNumber, value);
			}
		}
	}

	public void bind(String bindVariableName, double value) throws SQLException {
		if (rset != null) {
			rset.close();
			rset = null;
		}
		ArrayList<Integer> bindNumbers = getBindNumbers(bindVariableName);
		if (bindNumbers == null) {
			illegalBindVariable(bindVariableName);
		} else {
			for (Integer bindNumber : bindNumbers) {
				stmt.setDouble(bindNumber, value);
			}
		}
	}

	public void bind(String bindVariableName, int value) throws SQLException {
		if (rset != null) {
			rset.close();
			rset = null;
		}
		ArrayList<Integer> bindNumbers = bindVariables.get(bindVariableName
				.toUpperCase());
		if (bindNumbers == null) {
			illegalBindVariable(bindVariableName);
		} else {
			for (Integer bindNumber : bindNumbers) {
				stmt.setInt(bindNumber, value);
			}
		}
	}

	public void bind(String bindVariableName, java.sql.Date value)
			throws SQLException {
		rset.close();
		rset = null;
		ArrayList<Integer> bindNumbers = getBindNumbers(bindVariableName);
		if (bindNumbers == null) {
			illegalBindVariable(bindVariableName);
		} else {
			for (Integer bindNumber : bindNumbers) {
				logger.info("binding '" + bindVariableName + "' index "
						+ bindNumber);
				stmt.setDate(bindNumber, value);
			}
		}
	}

	public void bind(String bindVariableName, String value) throws SQLException {
		if (rset != null) {
			rset.close();
			rset = null;
		}
		ArrayList<Integer> bindNumbers = getBindNumbers(bindVariableName);
		if (bindNumbers == null) {
			illegalBindVariable(bindVariableName);
		} else {
			for (Integer bindNumber : bindNumbers) {
				logger.info("binding '" + bindVariableName + "' index "
						+ bindNumber);
				stmt.setString(bindNumber, value);
			}
		}
	}

	public void bindNull(String bindVariableName) throws SQLException {
		ArrayList<Integer> bindNumbers = bindVariables.get(bindVariableName
				.toUpperCase());
		if (bindNumbers != null) {
			for (Integer bindNumber : bindNumbers) {
				logger.info("binding '" + bindVariableName + "' index "
						+ bindNumber);
				stmt.setNull(bindNumber, Types.NULL);
			}
		}
	}

	/**
	 * @todo this name sucks, too many side effects.
	 */
	public void clearBindVariables() throws SQLException {
		if (rset != null) {
			rset.close();
		}
		rset = null;
	}

	public void dispose() throws SQLException {
		if (rset != null) {
			rset.close();
		}
		if (stmt != null) {
			stmt.close();
		}
	}

	/**
	 * Formats the output of the select statement as XML.
	 * 
	 * Caveat - if the select statement returns two columns with the same name
	 * only the last one is used as an attribute.
	 * 
	 * @todo don't close the string writer
	 * @deprecated use Dexter
	 * @param wrapperElementName
	 * @param rowElementName
	 * @param asElements
	 *            true - data
	 * @throws SQLException
	 * @throws IOException
	 */
	@Deprecated
	public void getAsXml(String wrapperElementName, String rowElementName,
			boolean asElements, Writer sw) throws SQLException, IOException {
		// check arguments
		if (sw == null) {
			throw new IllegalArgumentException("sw is null");
		}
		Document doc = DocumentFactory.getInstance().createDocument();
		Element root = doc.addElement(wrapperElementName);
		ResultSet rset = getResultSet();
		getColumnLabels(rset);
		addRows(rset, root, asElements, rowElementName);
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(sw, format);
		writer.write(doc);
		writer.close();
		stmt.close();
		// return returnValue;
	}

	public int getBindVariableCount() {
		return bindVariableCount;
	}

	public Set<String> getBindVariableNames() {
		return bindVariables.keySet();
	}

	public HashMap<String, ArrayList<Integer>> getBindVariables() {
		return bindVariables;
	}

	public HashMap<String, BindVariableValue> getBindVariableValues() {
		return bindVariableValues;
	}

	public HashMap<String, String> getCamelMap() {
		return camelMap;
	}

	public ArrayList<String> getColumnLabels() {
		return columnLabels;
	}

	public HashSet<String> getColumnNames() {
		return columnNames;
	}

	public String getDateBucketDestinationColumnName() {
		return dateBucketDestinationColumnName;
	}

	public String getDateBucketSourceColumnName() {
		return dateBucketSourceColumnName;
	}

	public HashMap<String, String> getInvertedCamel() {
		return invertedCamel;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public Integer getMaxQuerySec() {
		return maxQuerySec;
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		if (stmt == null) {
			throw new IllegalStateException("stmt has not been prepared");
		}
		ResultSetMetaData meta = stmt.getMetaData();
		return meta;
	}

	public Element getMetaDataAsElement() throws SQLException {

		return ResultSetMetaDataHelper.asElement(getMetaData());
	}

	public Object getNextRow(ResultSetPopulable object) throws SQLException {
		Object returnValue = null;
		getResultSet();
		if (rset.next()) {
			rowCount++;
			object.setValues(rset);
			returnValue = object;
		}
		return returnValue;
	}

	// @todo add binds
	public Element getRequestAsXmlElement() throws SQLException {
		Element el = DocumentHelper.createElement("SelectHelper");
		Element selectEl = el.addElement("selectText");
		selectEl.setText(selectText);
		el.add(getMetaDataAsElement());
		return el;
	}

	/**
	 * @todo make sure state is correct
	 * @return ResultSet If called after some rows have been fetched, not all
	 *         rows will be present.
	 */
	public ResultSet getResultSet() throws SQLException {
		if (rset == null) {
			try {
				executingQuery = true;

				if (maxQuerySec != null) {
					if (events.exists(SHOW_QUERY_TIMEOUT)) {
						logger.debug("setting queryTimeout " + maxQuerySec);
					}
					stmt.setQueryTimeout(maxQuerySec);
				}
				long beforeExecuteNano = System.nanoTime();
				rset = stmt.executeQuery();
				long afterExecuteNano = System.nanoTime();
				executeQueryMillis = (afterExecuteNano - beforeExecuteNano)
						/ (1000 * 1000);
				// todo now that I am using log4j create and pass and stats
				// collection
				// todo multiplexed output is unreadable
				if (logger.isDebugEnabled()) {
					logger.debug("execute query took " + executeQueryMillis
							+ " milliseconds");
				}
				if (events.exists(RSET_AVAILABLE)) {
					logger.debug("executeQuery complete");
				}
				executingQuery = false;

			} catch (SQLException e) {
				throw new SQLException("while processing: '\n" + selectText
						+ "'\n" + e.getMessage());
			}
		}
		return rset;
	}

	public ResultSet getResultSetNoBind() throws SQLException {
		for (String bindVariableName : bindVariables.keySet()) {
			bindNull(bindVariableName);
		}
		if (maxQuerySec != null) {
			logger.info("setting queryTimeout " + maxQuerySec);
			stmt.setQueryTimeout(maxQuerySec);
		}
		rset = stmt.executeQuery();
		return rset;
	}

	public long getRowCount() {
		return rowCount;
	}

	public ResultSet getRset() {
		return rset;
	}

	public String getSelectText() {
		return selectText;
	}

	public PreparedStatement getStmt() {
		return stmt;
	}

	public boolean isAllowUnbound() {
		return allowUnbound;
	}

	public boolean isCamelCase() {
		return camelCase;
	}

	public boolean isDebug() {
		return debug;
	}

	public boolean isIgnoreUnnecessaryBinds() {
		return ignoreUnnecessaryBinds;
	}

	public void setAllowUnbound(boolean allowUnbound) {
		this.allowUnbound = allowUnbound;
	}

	/**
	 * Checks the specified values against the statement
	 * <ul>
	 * <li>ensure that every value is a variable specified in the sql.</li>
	 * <li>ensure that no value is duplicated</li>
	 * </ul>
	 * Checks that all variables are bound
	 * <ul>
	 * <li>every bind variable :xxx toke in statement not in quotes must be in
	 * the list of bind values.</li>
	 * </ul>
	 * 
	 * @todo add case sensitivity option
	 * @param binds
	 * @throws SQLException
	 */
	public void setBindVariableValues(Collection<BindVariableValue> binds)
			throws SQLException {
		ArrayList<String> unboundVariables = null;
		bindVariableValues.clear();
		if (binds == null) {
			throw new IllegalArgumentException("binds is null");
		}
		// @todo review - commented out unreachable review
		// if (binds != null) {
		// for (BindVariableValue bind : binds) {
		// BindVariableValue prev = bindVariableValues.put(bind
		// .getVariableName(), bind);
		// if (prev != null) {
		// StringBuilder b = new StringBuilder();
		// for (BindVariableValue bnd : binds) {
		// b.append(bnd.getVariableName());
		// b.append(":");
		// b.append(bnd.getValue());
		// b.append("\n");
		// }
		// Thread.dumpStack();
		// throw new IllegalArgumentException(
		// "duplicate bind variable " + bind.getVariableName()
		// + "\n" + b.toString());
		// }
		// if (bindVariables.get(bind.getVariableName()) == null
		// && logBindVariablesNotInSelect) {
		// String message = "bind variable: '"
		// + bind.getVariableName()
		// + "' is not in the select statement" + "\n"
		// + toString();
		// logger.warn(message);
		// // throw new IllegalArgumentException(message);
		// }
		// }
		// }
		// check that all variables are bound
		for (String variableName : bindVariables.keySet()) {
			if (bindVariableValues.get(variableName) == null) {
				if (unboundVariables == null) {
					unboundVariables = new ArrayList<String>();
				}
				unboundVariables.add(variableName);
			}
		}
		// report unbound variables
		if (!allowUnbound) {
			if (unboundVariables != null) {
				StringBuffer buff = new StringBuffer();
				buff.append("variablesNotBound: ");
				for (String unbound : unboundVariables) {
					buff.append(unbound);
					buff.append(" ");
				}
				throw new IllegalArgumentException(buff.toString());
			}
		}
		if (unboundVariables != null) {
			for (String nm : unboundVariables) {
				logger.debug("binding null " + nm);
				bindNull(nm);
			}
		}
		// bind values to variables

		for (BindVariableValue bind : binds) {

			ArrayList<Integer> bindNumbers = bindVariables.get(bind
					.getVariableName());
			if (bindNumbers == null && !allowUnbound) {
				String message = "bindVariables have not been set and allowUnbound is"
						+ allowUnbound;
				logger.warn(message);
				throw new IllegalArgumentException(message);
			}
			if (bind.getValue() == null) {
				bindNull(bind.getVariableName());
			} else {
				if (bindNumbers != null) {
					for (Integer bindNumber : bindNumbers) {

						Object o = bind.getValueObject();
						if (logger.isDebugEnabled()) {
							if (o != null) {
								String msg = "binding number " + bindNumber
										+ " to "
										+ bind.getValueObject().toString();
								logger.debug(msg);
							} else {
								String msg = "binding number " + bindNumber
										+ " to null";
								logger.debug(msg);
							}
						}
						stmt.setObject(bindNumber, bind.getValueObject());
					}
				} else {
					if (logBinds) {
						logger.info("no binds found in statement");
					}
				}
			}
		}
	}

	/**
	 * TODO DOCUMENT THIS.
	 * 
	 * @param dateBucketer
	 * @param sourceColumnName
	 * @param destinationColumnName
	 */
	public void setDateGenerator(DateGenerator dateBucketer,
			String sourceColumnName, String destinationColumnName) {
		this.dateBucketSourceColumnName = sourceColumnName;
		this.dateBucketDestinationColumnName = destinationColumnName;
	}

	public void setIgnoreUnnecessaryBinds(boolean ignoreUnnecessaryBinds) {
		this.ignoreUnnecessaryBinds = ignoreUnnecessaryBinds;
	}

	public void setLogLevel(Level level) {
		logger.setLevel(level);
	}

	// public void setLogLevel(String level) {
	// logger.setLevel(LoggerHelper.toLevel(level));
	// }

	public void setMaxQuerySec(Integer maxQuerySec) {
		this.maxQuerySec = maxQuerySec;
	}

	public void setVerbose(boolean val) {
		this.verbose = val;
	}

	@Override
	public String toString() {
		StringBuilder buff = new StringBuilder(2048);
		buff.append(getBindVariablesAsString());
		buff.append(getBindValuesAsString());
		buff.append("select\n" + selectText);
		return buff.toString();
	}

	private void addBindVariable(String bindVariableName) {
		ArrayList<Integer> bindIndexes = bindVariables.get(bindVariableName
				.toUpperCase());
		if (bindIndexes == null) {
			bindIndexes = new ArrayList<Integer>();
		}
		bindVariableCount++;
		bindIndexes.add(bindVariableCount); // new
		bindVariables.put(bindVariableName.toUpperCase(), bindIndexes);
	}

	/**
	 * Adds the ResultSet data to the xml Document.
	 * 
	 * Conversions<br>
	 * <ul>
	 * <li>java.sql.Types.DATE - yyyy-mm-dd</li>
	 * <li>java.sql.Types.TIMESTAMP - yyyy-mm-dd hh24:MI:SS</li>
	 * </ul>
	 * If the dataType is returned as a java.sql.Types.DATE it is formatted as
	 * java.sql.Date.toString, ie. yyyy-mm-dd and any time components are
	 * discarded.
	 * 
	 * To get the time component, perform a to_timestamp on the column. select
	 * to_timestamp(sysdate) now from dual;
	 * 
	 * The selects should not do a to_char(sysdate,'rr-mon-dd HH:MM') type
	 * conversion in order to allow xslt to convert the format to a locale
	 * specific representation.
	 * 
	 * @param rset
	 * @param root
	 * @param asElements
	 * @param rowElementName
	 * @throws SQLException
	 * @deprecated use Dexter
	 */
	@Deprecated
	private void addRows(ResultSet rset, Element root, boolean asElements,
			String rowElementName) throws SQLException {
		ResultSetMetaData meta = rset.getMetaData();
		while (rset.next()) {
			Element rowEl = root.addElement(rowElementName);
			for (int column = 1; column <= meta.getColumnCount(); column++) {
				String data = rset.getString(column);
				if (data != null) {
					switch (meta.getColumnType(column)) {
					case Types.DATE:
						data = rset.getDate(column).toString();
						break;
					case Types.TIMESTAMP: // get as java.sql.TimeStamp not
						// oracle.sql.Timestamp
						Timestamp t = rset.getTimestamp(column);
						data = t.toString().substring(0, 16);
						break;
					}
				}
				if (data != null) {
					if (asElements) {
						rowEl.addElement(columnLabels.get(column - 1), data);
					} else {
						rowEl.addAttribute(columnLabels.get(column - 1), data);
					}
				}
			}
		}
	}

	private String camelMap(String name) {
		String returnValue;
		if (camelMap == null) {
			camelMap = new HashMap<String, String>();
		}
		if (invertedCamel == null) {
			invertedCamel = new HashMap<String, String>();
		}
		returnValue = camelMap.get(name);
		if (returnValue == null) {
			returnValue = StringHelper.attributeName(name);
			camelMap.put(name, returnValue);
		}
		String check = invertedCamel.get(returnValue);
		if (check == null) {
			invertedCamel.put(returnValue, name);
		} else {
			if (!check.equals(name)) {
				throw new IllegalStateException(
						"two values map to same camelCase");
			}
		}
		return returnValue;
	}

	private ArrayList<Integer> getBindNumbers(String bindVariableName) {
		ArrayList<Integer> returnValue = bindVariables.get(bindVariableName
				.toUpperCase());

		if (returnValue == null) {
			if (ignoreUnnecessaryBinds) {
				returnValue = new ArrayList<Integer>();
			} else {
				StringBuffer buff = new StringBuffer();
				buff.append("no such bind variable '");
				buff.append(bindVariableName);
				buff.append("' known bind variables are:\n");
				for (String var : bindVariables.keySet()) {
					buff.append("  '");
					buff.append(var);
					buff.append("'\n");
				}
				throw new IllegalArgumentException(buff.toString());
			}
		}
		return returnValue;
	}

	private String getBindValuesAsString() {
		StringBuilder buff = new StringBuilder();
		for (BindVariableValue bind : bindVariableValues.values()) {
			buff.append("name: ");
			buff.append(bind.getVariableName());
			buff.append(" type: ");
			buff.append(bind.getJdbcType());
			buff.append(" value: ");
			buff.append(bind.getValueObject().toString());
			buff.append("\n");
		}
		return buff.toString();
	}

	private String getBindVariablesAsString() {
		StringBuilder buff = new StringBuilder();
		for (String name : bindVariables.keySet()) {
			buff.append("name: ");
			buff.append(name);
			buff.append("\n");
		}
		return buff.toString();
	}

	private void getColumnLabels(ResultSet rset) throws SQLException {
		ResultSetMetaData meta = rset.getMetaData();
		for (int column = 1; column <= meta.getColumnCount(); column++) {
			String name = meta.getColumnName(column);
			boolean previous = columnNames.add(name);
			if (!previous) {
				logger.warn("name " + name
						+ " occurs more than once in select statement");
			}
			if (camelCase) {
				columnLabels.add(camelMap(name));
			} else {
				columnLabels.add(name);
			}
		}
	}

	private void illegalBindVariable(String bindVariableName) {
		if (!ignoreUnnecessaryBinds) {
			throw new IllegalArgumentException("no such bind variable '"
					+ bindVariableName);
		}
	}

	@SuppressWarnings("unused")
	// @todo implement or delete
	private void log(ArrayList<BindVariableValue> binds) {
		if (debug) {
			for (BindVariableValue bind : binds) {
				logger.debug("name:" + bind.getVariableName() + " type "
						+ bind.getJdbcType() + " value "
						+ bind.getValueObject().toString());
			}
		}
	}

	// public final static Pattern finder = Pattern.compile("")
	// @todo pukes if a '\' is found in the SQL
	void parseBindVariables() {
		@SuppressWarnings("unused")
		String leading = null;
		String delim = null;
		String trailing = null;
		bindVariables = new HashMap<String, ArrayList<Integer>>();
		// boolean moreTokens = true;
		String unparsed = selectText.replaceAll("\n", " ")
				.replaceAll("\r", " ");

		while (unparsed.length() > 0) {
			Matcher tokenMatcher = tokenFinder.matcher(unparsed);
			tokenMatcher.useAnchoringBounds(false);
			if (tokenMatcher.find()) {
				leading = tokenMatcher.group(1);
				delim = tokenMatcher.group(2);
				trailing = tokenMatcher.group(3);
				unparsed = trailing;
			} else {
				break;
			}
			char delimiter = delim.charAt(0);
			switch (delimiter) {
			case ':':
				Matcher bindNameMatcher = bindNameFinder.matcher(unparsed);
				bindNameMatcher.find();
				String bindVariable = bindNameMatcher.group(1);
				unparsed = bindNameMatcher.group(2);
				addBindVariable(bindVariable.toUpperCase());
				break;
			case '"':
				Matcher dquoteMatcher = doubleQuoteEndFinder.matcher(trailing);
				boolean found = dquoteMatcher.find();
				if (!found) {
					logger.error("couldn't get a match for ending quote on '"
							+ trailing);
				}
				unparsed = dquoteMatcher.group(2);
				break;
			case '\'':
				Matcher quoteMatcher = quoteEndFinder.matcher(trailing);
				quoteMatcher.find();
				unparsed = quoteMatcher.group(2);
				break;
			}
		}
	}

	private void parseBindVariables2() {
		@SuppressWarnings("unused")
		String leading = null;
		String delim = null;
		String trailing = null;
		bindVariables = new HashMap<String, ArrayList<Integer>>();
		// boolean moreTokens = true;
		String unparsed = selectText.replaceAll("\n", " \n ").replaceAll("\r",
				" \r ");

		while (unparsed.length() > 0) {
			Matcher tokenMatcher = tokenFinder.matcher(unparsed);
			tokenMatcher.useAnchoringBounds(false);
			if (tokenMatcher.find()) {
				leading = tokenMatcher.group(1);
				delim = tokenMatcher.group(2);
				trailing = tokenMatcher.group(3);
				unparsed = trailing;
			} else {
				break;
			}
			if ("--".equals(delim)) {

			}
			char delimiter = delim.charAt(0);
			switch (delimiter) {
			case ':':
				Matcher bindNameMatcher = bindNameFinder.matcher(unparsed);
				bindNameMatcher.find();
				String bindVariable = bindNameMatcher.group(1);
				unparsed = bindNameMatcher.group(2);
				addBindVariable(bindVariable.toUpperCase());
				break;
			case '"':
				Matcher dquoteMatcher = doubleQuoteEndFinder.matcher(trailing);
				boolean found = dquoteMatcher.find();
				if (!found) {
					logger.error("couldn't get a match for ending quote on '"
							+ trailing);
				}
				unparsed = dquoteMatcher.group(2);
				break;
			case '\'':
				Matcher quoteMatcher = quoteEndFinder.matcher(trailing);
				quoteMatcher.find();
				unparsed = quoteMatcher.group(2);
				break;
			}
		}
	}
}
