package org.javautil.dex.dexterous;

// @todo write regression tests
// @todo show how to get dense dates on crosstab
// @todo command line dexterous no longer puts out the xml header
// @todo dexterous2 report no longer transforms
// @todo write only to WriterSet and Switch the writer set based on whether a transform is occurring

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.javautil.dex.DbexpertsEnvironment;
import org.javautil.dex.DexterousStateException;
import org.javautil.dex.EventHelper;
import org.javautil.dex.bsh.Cursor;
import org.javautil.dex.parser.NodeRelationship;
import org.javautil.dex.parser.Parser;
import org.javautil.dex.renderer.ExcelXmlWriter;
import org.javautil.document.MimeType;
import org.javautil.io.WriterSet;
import org.javautil.io.WriterWrapper;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;
import org.javautil.jdbc.JdbcType;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.InvalidDataSourceException;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;
import org.javautil.persistence.PersistenceException;

import bsh.NameSpace;
import bsh.UtilEvalError;
import bsh.classpath.ClassManagerImpl;

//...

/**
 * Enhancement history
 * 	2007-08-04 Added support for <% script delimiters
 *             Added show waits
 *             Added show stats
 *             Added spool to Dexterous in order to support
 */
/**
 * @todo add verify mode that just checks that the script works
 * @todo check why statement on connection is not working
 * @todo add verbosity levels for use in verify and for checking progress @ todo
 *       if a sql file is specified from the command line, caching should not
 * @todo set file log level FINE
 * @todo set log directory DIRECTORY_NAME
 * @todo set stdout log level FINE
 * @todo config sticky on connection dexterous property under connections and
 *       per connection
 * @todo writeln writes to spool file valid on what mimes?
 * @todo support dexterous command in connection
 * @todo support security scripts for authentication and rls
 * @todo support dexterous function declaration and invocation
 * @todo get a separate connection for getting waits and stats
 * @todo
 * 
 * 
 * @todo trap sql exception and wrap
 * @author jim
 * 
 */

public class State implements DexterousState {
	// private static TreeMap<String, File> queryCache = new HashMap<String,
	// File>();

	private SimpleDateFormat dateFormatter = new SimpleDateFormat("mm-dd-yyyy");

	private final EventHelper events = new EventHelper();

	private static String newline = System.getProperty("line.separator");

	private static EventHelper traceEvents = new EventHelper();

	private Integer allowableQueryMilliseconds;

	// private DexterousArguments arguments;

	private boolean autoTrace = false;

	private String baseDirectoryName;

	private Map<String, BindVariableValue> bindValues = new TreeMap<String, BindVariableValue>();

	private final Map<String, BindVariable> bindVariables = new TreeMap<String, BindVariable>();

	private ArrayList<String> breaks = null;

	private boolean closeWriters = true;

	private final ClassManagerImpl cm = new ClassManagerImpl();

	private Connection conn;

	// private Appender console;

	private Category consoleAppender;

	private List<String> crossTabColumns = null;

	private List<String> crossTabRows = null;

	private List<String> crossTabValues = null;

	private Element currentElement;

	private File currentFile;

	private DataSource dataSource;

	private DataSources dataSources;

	private String dateFormat;

	private String defaultDataSourceName;

	private String dexterLogFileName;

	private Writer dexterLogWriter;

	private Document document;

	private Document documentTemplate;

	private boolean echo = false;

	/**
	 * Should resultSet MetaData be emitted when writing XML?
	 */
	private boolean emitMetaData = false;

	// private boolean emitXmlAsElement;

	private boolean endOfInput = false;

	private SimpleDateFormat fileDateFormatter;

	private final Stack<FileState> fileStack = new Stack<FileState>();

	private File logDirectory;

	private String logDirectoryName;

	private final Logger logger = Logger.getLogger(getClass().getName());

	private boolean makePaths = false;

	// @todo allow this to be set
	private final int maxSqlExceptionCount = 100;

	private MimeType mimeType = MimeType.CSV;

	private final NameSpace namespace = new NameSpace(cm, "dexterous");

	private Parser parser;

	private LineNumberReader reader;

	private Collection<File> requestFiles;

	private final ArrayList<String> reservedWords = new ArrayList<String>();

	private String resultSetId;

	private final long rowCount = -1;

	private int sqlExceptionCount = 0;

	private boolean sqlIsSelect;

	private String sqlText;

	private final StringStores stringStores = new StringStores();

	private String stylesheetFileName;

	@SuppressWarnings("unused")
	private boolean termout = true;

	private String traceFileName;

	private String traceId;

	private Logger traceLogger;

	private boolean useCachedIfAvailable = false;

	private boolean useCacheThisRequest = false;

	private HSSFWorkbook workbook;

	private String worksheetName;

	// todo what is this and why
	// private NodeRelationship currentElementRelationship;

	private WriterSet writers = new WriterSet("dexterous default");

	private String htmlElementName;

	private boolean emitXmlAsElement;

	private ExcelXmlWriter xmlWorkbook;

	private ExcelXmlWriter excelXmlWriter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#addBindVariable(java.lang.String
	 * , org.javautil.jdbc.BindVariable)
	 */
	@Override
	public void addBindVariable(final String variableName, final BindVariable bv) {
		bindVariables.put(variableName, bv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#addBindVariable(java.lang.String
	 * , org.javautil.jdbc.JdbcType, java.lang.String)
	 */
	@Override
	public void addBindVariable(final String bindName, final JdbcType jdbcType,
			final String value) {
		bindValues.put(bindName, new BindVariableValue(bindName, jdbcType,
				value));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#addStringStore(java.lang.String
	 * )
	 */
	@Override
	public void addStringStore(final String columnName) {
		if (columnName == null) {
			throw new IllegalArgumentException("columnName is null");
		}
		stringStores.addStore(columnName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#clearBindVariables()
	 */
	@Override
	public void clearBindVariables() {
		bindVariables.clear();
	}

	// TODO fix the number of places writer wrappers get closed this is a hack
	void clearNonStdoutWriters() throws IOException {
		final WriterWrapper[] it = writers.getWriters();
		for (int i = 0; i < it.length; i++) {
			final WriterWrapper w = it[i];
			if (w.getDescription().equals("stdout")) { // TODO nasty hack
				continue;
			}
			w.flush();
			w.close();
			writers.remove(w);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#closeWriters()
	 */
	@Override
	public void closeWriters() throws IOException {

		writers.close();

	}

	// @todo why am I carrying a connection around without it being a javautil
	// Connection
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#createCursor(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public Cursor createCursor(final String cursorName, final String mySqlText,
			final String recordName) throws UtilEvalError, PersistenceException {

		final Cursor c = new Cursor(conn, cursorName, mySqlText, namespace,
				recordName);
		namespace.setVariable(cursorName, c, false);
		return c;
	}

	// /
	// / Acccessors
	// /
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#getAllowableQueryMilliseconds()
	 */
	@Override
	public Integer getAllowableQueryMilliseconds() {
		return allowableQueryMilliseconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getBaseDirectoryName()
	 */
	@Override
	public String getBaseDirectoryName() {
		return baseDirectoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getBindValues()
	 */
	@Override
	public Map<String, BindVariableValue> getBindValues() {
		return bindValues;
	}

	// @todo switch to user the namespace
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#getBindVariable(java.lang.String
	 * )
	 */
	@Override
	public BindVariable getBindVariable(final String name) {
		return bindVariables.get(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getBreaks()
	 */
	@Override
	public ArrayList<String> getBreaks() {
		return breaks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getCrossTabColumns()
	 */
	@Override
	public List<String> getCrosstabColumns() {
		return crossTabColumns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getCrossTabRows()
	 */
	@Override
	public List<String> getCrosstabRows() {
		return crossTabRows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getCrossTabValues()
	 */
	@Override
	public List<String> getCrosstabValues() {
		return crossTabValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getCurrentFile()
	 */
	@Override
	public File getCurrentFile() {
		return currentFile;
	}

	// String getCurrentRow() {
	// return currentRow;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getDataSource()
	 */
	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	private DataSources getDataSources() throws SQLException {
		// todo use configuration name if provided
		if (dataSources == null) {
			dataSources = new SimpleDatasourcesFactory();
		}
		return dataSources;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getDateFormat()
	 */
	@Override
	public String getDateFormat() {
		return dateFormat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getDefaultDataSourceName()
	 */
	@Override
	public String getDefaultDataSourceName() {
		return defaultDataSourceName;
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see org.javautil.dex.dexterous.DexterousState#getDexter()
	// */
	// public Dexter getDexter() {
	// return dexter;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getDexterLogFileName()
	 */
	@Override
	public String getDexterLogFileName() {
		return dexterLogFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getDexterLogWriter()
	 */
	@Override
	public Writer getDexterLogWriter() {
		return dexterLogWriter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getEcho()
	 */
	@Override
	public boolean getEcho() {
		return echo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getFileDateFormatter()
	 */
	@Override
	public SimpleDateFormat getFileDateFormatter() {
		if (fileDateFormatter == null) {
			fileDateFormatter = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
		}
		return fileDateFormatter;
	}

	@Override
	public LineNumberReader getInputReader() {
		return reader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getLogDirectory()
	 */
	// public File getLogDirectory() {
	// File retval = null;
	// while (true) {
	// if (logDirectory != null) {
	// retval = logDirectory;
	// break;
	// }
	// if (arguments.getLogDirectory() != null) {
	// retval = arguments.getLogDirectory();
	// break;
	// }
	// if (retval == null) {
	// retval = new File(".");
	// }
	// break;
	// }
	// logger.debug("log directory is '" + retval + "' which resolves to " +
	// retval.getAbsolutePath());
	// return retval;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getMimeType()
	 */
	@Override
	public MimeType getMimeType() {
		return mimeType;
	}

	// /**
	// * @param eventLevel
	// * the eventLevel to set
	// */
	// public void setEventLevel(EventLevel eventLevel) {
	// this.eventLevel = eventLevel;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getResultSetId()
	 */
	@Override
	public String getResultSetId() {
		return resultSetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getSqlText()
	 */
	@Override
	public String getSqlText() {
		return sqlText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getStylesheetFileName()
	 */
	@Override
	public String getStylesheetFileName() {
		return stylesheetFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getTraceFileName()
	 */
	@Override
	public String getTraceFileName() {
		return traceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getTraceId()
	 */
	@Override
	public String getTraceId() {
		return traceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getTraceLogger()
	 */
	@Override
	public Logger getTraceLogger() {
		return traceLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getWorkbook()
	 */
	@Override
	public HSSFWorkbook getWorkbook() {
		return workbook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getWorksheetName()
	 */
	@Override
	public String getWorksheetName() {
		return worksheetName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#getWriterDescriptions()
	 */
	@Override
	public String getWriterDescriptions() {
		return writers.getWriterDescriptions();

	}

	@Override
	public WriterSet getWriters() {
		return writers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isAutoTrace()
	 */
	@Override
	public boolean isAutoTrace() {
		return autoTrace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isCloseWriters()
	 */
	@Override
	public boolean isCloseWriters() {
		return closeWriters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isEmitMetaData()
	 */
	@Override
	public boolean isEmitMetaData() {
		return emitMetaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isEndOfInput()
	 */
	@Override
	public boolean isEndOfInput() {
		return endOfInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isMakePaths()
	 */
	@Override
	public boolean isMakePaths() {
		return makePaths;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isTermout()
	 */
	@Override
	public boolean isTermout() {
		return termout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isUseCachedIfAvailable()
	 */
	@Override
	public boolean isUseCachedIfAvailable() {
		return useCachedIfAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#isUseCacheThisRequest()
	 */
	@Override
	public boolean isUseCacheThisRequest() {
		return useCacheThisRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setAllowableQueryMilliseconds
	 * (java.lang.Long)
	 */
	@Override
	public void setAllowableQueryMilliseconds(
			final Integer allowableQueryMilliseconds) {
		this.allowableQueryMilliseconds = allowableQueryMilliseconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#
	 * setAllowableQueryTimeoutMilliseconds(java.lang.Long)
	 */
	public void setAllowableQueryTimeoutMilliseconds(final Integer l) {
		this.allowableQueryMilliseconds = l;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setAutoTrace(boolean)
	 */
	@Override
	public void setAutoTrace(final boolean autoTrace) {
		this.autoTrace = autoTrace;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBaseDirectoryName(java.lang
	 * .String)
	 */
	@Override
	public void setBaseDirectoryName(final String baseDirectoryName) {
		this.baseDirectoryName = baseDirectoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBindValues(java.util.HashMap
	 * )
	 */
	@Override
	public void setBindValues(final Map<String, BindVariableValue> bindValues) {
		this.bindValues = bindValues;
	}

	// @todo stop using bindValues and just use the namespace
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBindVariable(java.lang.String
	 * , org.javautil.jdbc.BindVariableValue)
	 */
	@Override
	public void setBindVariable(final String variableName,
			final BindVariableValue value) throws UtilEvalError {
		if (variableName == null) {
			throw new IllegalArgumentException("variableName is null");
		}
		if (value == null) {
			throw new IllegalArgumentException("value is null");
		}
		bindValues.put(variableName, value);
		namespace.setVariable(variableName, value.getValueObject(), false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBindVariables(javax.servlet
	 * .http.HttpServletRequest)
	 */
	@Override
	public void setBindVariables(final HttpServletRequest request) {
		final StringBuilder sb = new StringBuilder();
		sb.append("bind variables are ");
		for (final BindVariable bf : bindVariables.values()) {
			sb.append("'" + bf.getBindName() + "'");
		}
		final Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			final String n = e.nextElement();
			final String v = request.getParameter(n);
			final String bindName = n.toUpperCase();
			if (reservedWords.contains(bindName)) {
				continue;
			}
			bindValues.put(bindName, new BindVariableValue(bindName,
					JdbcType.STRING, v));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBindVariablesFromNamespace()
	 */
	// @todo check for lower case variable names and name case collision
	@Override
	public void setBindVariablesFromNamespace() {
		for (final String name : namespace.getAllNames()) {
			Object o;
			try {
				o = namespace.getVariable(name);
				if (!(o instanceof bsh.Primitive)) {
					final BindVariableValue bvv = new BindVariableValue(
							name.toUpperCase(), o);
					bindValues.put(name.toUpperCase(), bvv);
				}
			} catch (final UtilEvalError e) {
				logger.error("not likely since we were enumerating");
				throw new IllegalStateException(e.getMessage()
						+ " while getting '" + name + "'");
			}
			logger.debug("bind " + name + ":" + o.toString() + " "
					+ o.getClass().getName());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setBreaks(java.util.ArrayList)
	 */
	@Override
	public void setBreaks(final ArrayList<String> _breaks) {
		this.breaks = _breaks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setCloseWriters(boolean)
	 */
	@Override
	public void setCloseWriters(final boolean closeWriters) {
		this.closeWriters = closeWriters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setConnection(java.sql.Connection
	 * )
	 */
	@Override
	public void setConnection(final Connection conn) {
		this.conn = conn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setConsoleHandlerLogLevel(org
	 * .apache.log4j.Level)
	 */
	@Override
	public void setConsoleHandlerLogLevel(final Level level) {
		consoleAppender.setLevel(level);
		logger.debug("stdout log level set to " + level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setCrossTabColumns(java.util
	 * .List)
	 */
	@Override
	public void setCrossTabColumns(final List<String> crossTabColumns) {
		this.crossTabColumns = crossTabColumns;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setCrossTabRows(java.util.List)
	 */
	@Override
	public void setCrossTabRows(final List<String> name) {
		this.crossTabRows = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setCrossTabValues(java.util
	 * .List)
	 */
	@Override
	public void setCrossTabValues(final List<String> crossTabValues) {
		this.crossTabValues = crossTabValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDataSource(javax.sql.DataSource
	 * )
	 */
	@Override
	public void setDataSource(final DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDataSourceName(java.lang
	 * .String)
	 */
	@Override
	public void setDataSourceName(final String dsn)
			throws InvalidDataSourceException, SQLException {
		if (dsn != null) {
			conn = getDataSources().getDataSource(dsn).getConnection();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDataSources(com.dbexperts
	 * .jdbc.DataSources)
	 */
	@Override
	public void setDataSources(final DataSources dataSources) {
		this.dataSources = dataSources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDateFormat(java.lang.String)
	 */
	@Override
	public void setDateFormat(final String dateFormat) {
		this.dateFormat = dateFormat;
		dateFormatter = new SimpleDateFormat(dateFormat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDefaultDataSourceName(java
	 * .lang.String)
	 */
	@Override
	public void setDefaultDataSourceName(final String defaultDataSourceName) {
		this.defaultDataSourceName = defaultDataSourceName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDexterLogFileName(java.lang
	 * .String)
	 */
	@Override
	public void setDexterLogFileName(final String dexterLogFileName) {
		this.dexterLogFileName = dexterLogFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDexterLogWriter(java.io.
	 * Writer)
	 */
	@Override
	public void setDexterLogWriter(final Writer dexterLogWriter) {
		this.dexterLogWriter = dexterLogWriter;
	}

	// /**
	// * @return the eventLevel
	// */
	// public EventLevel getEventLevel() {
	// return eventLevel;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDocumentTemplate(java.lang
	 * .String)
	 */
	@Override
	public void setDocumentTemplate(final String fileName)
			throws DocumentException {
		final SAXReader xmlReader = new SAXReader();

		this.documentTemplate = xmlReader.read(fileName);
		if (this.document != null) {
			throw new IllegalStateException(
					"a document is already open and hasn't been written");
		}
		this.document = documentTemplate;
		// TODO should serialize the document template to prevent having to
		// read and parse again and reuse if the
		// file checksum matches,
		// need to close out the document after writing the output
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setDocumentTemplateXpath(java
	 * .lang.String, org.javautil.dex.parser.NodeRelationship, boolean)
	 */
	@Override
	public void setDocumentTemplateXpath(final String xpath,
			final NodeRelationship nodeRelationship, final boolean asElement) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setEcho(boolean)
	 */
	@Override
	public void setEcho(final boolean val) {
		echo = val;
	}

	//
	//
	// Cache routines
	//

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setEmitMetaData(boolean)
	 */
	@Override
	public void setEmitMetaData(final boolean emitMetaData) {
		this.emitMetaData = emitMetaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setEndOfInput(boolean)
	 */
	@Override
	public void setEndOfInput(final boolean endOfInput) {
		this.endOfInput = endOfInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setFileDateFormat(java.lang
	 * .String)
	 */
	@Override
	public void setFileDateFormat(final String formatString) {
		this.fileDateFormatter = new SimpleDateFormat(formatString);
	}

	@Override
	public void setInputReader(final LineNumberReader r) {
		reader = r;// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setLogDirectory(java.lang.String
	 * )
	 */
	@Override
	public void setLogDirectory(final String token) {
		logDirectoryName = token;
		logDirectory = new File(token);
		if (!logDirectory.exists()) {
			throw new IllegalArgumentException("directory '" + logDirectoryName
					+ "' does not exist");
		}
		if (!logDirectory.canWrite()) {
			throw new IllegalArgumentException("can't write to log directory '"
					+ logDirectoryName + "'");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setLogLevel(org.apache.log4j
	 * .Level)
	 */
	@Override
	public void setLogLevel(final Level level) {
		logger.setLevel(level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setMakePaths(boolean)
	 */
	@Override
	public void setMakePaths(final boolean makePaths) {
		this.makePaths = makePaths;
	}

	@Override
	public void setMimeType(final MimeType mimeType) {
		this.mimeType = mimeType;

	}

	// @todo will have to add writes to cache
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setOutputStream(java.io.
	 * OutputStream)
	 */
	@Override
	public void setOutputStream(final OutputStream out) {
		writers = new WriterSet();
		writers.addWriter(out, "response", true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setParseUntilGo(boolean)
	 */
	@Override
	public void setParseUntilGo(final boolean parseUntilGo) {

	}

	@Override
	public void setRequestFile(final File sqlFile) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setResultSetId(java.lang.String
	 * )
	 */
	@Override
	public void setResultSetId(final String resultSetId) {
		this.resultSetId = resultSetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setSqlIsSelect(boolean)
	 */
	@Override
	public void setSqlIsSelect(final boolean val) {
		sqlIsSelect = val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setSqlText(java.lang.String)
	 */
	@Override
	public void setSqlText(final String sqlText) {
		this.sqlText = sqlText;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setStylesheetFileName(java.
	 * lang.String)
	 */
	@Override
	public void setStylesheetFileName(final String stylesheetFileName) {
		this.stylesheetFileName = stylesheetFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setTermout(boolean)
	 */
	@Override
	public void setTermout(final boolean termout) {
		this.termout = termout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setTraceFileName(java.lang.
	 * String)
	 */
	@Override
	public void setTraceFileName(final String traceFileName) {
		this.traceFileName = traceFileName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setTraceId(java.lang.String)
	 */
	@Override
	public void setTraceId(final String traceId) {
		this.traceId = traceId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setTraceLogger(org.apache.log4j
	 * .Logger)
	 */
	@Override
	public void setTraceLogger(final Logger traceLogger) {
		this.traceLogger = traceLogger;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.javautil.dex.dexterous.DexterousState#setTranspose(boolean)
	 */
	@Override
	public void setTranspose(final boolean transpose) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setUseCachedIfAvailable(boolean
	 * )
	 */
	@Override
	public void setUseCachedIfAvailable(final boolean useCachedIfAvailable) {
		this.useCachedIfAvailable = useCachedIfAvailable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setUseCacheThisRequest(boolean)
	 */
	@Override
	public void setUseCacheThisRequest(final boolean useCacheThisRequest) {
		this.useCacheThisRequest = useCacheThisRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setWorkbook(org.apache.poi.
	 * hssf.usermodel.HSSFWorkbook)
	 */
	@Override
	public void setWorkbook(final HSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setWorksheetName(java.lang.
	 * String)
	 */
	@Override
	public void setWorksheetName(final String worksheetName) {
		this.worksheetName = worksheetName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.javautil.dex.dexterous.DexterousState#setWriters(com.dbexperts.io
	 * .WriterSet)
	 */
	@Override
	public void setWriters(final WriterSet writers) {
		this.writers = writers;
	}

	@Override
	public EventHelper getEvents() {
		return events;
	}

	@Override
	public void addEvents(final Collection<Integer> events) {
		events.addAll(events);

	}

	@Override
	public Connection getConnection() throws InvalidDataSourceException,
			DexterousStateException {
		if (conn == null) {
			final String defaultDataSourceName = System
					.getenv(DbexpertsEnvironment.DEXTEROUS_DATA_SOURCE);
			if (defaultDataSourceName != null) {
				try {
					conn = dataSources.getDataSource(defaultDataSourceName)
							.getConnection();
				} catch (SQLException e) {
					throw new DexterousStateException(e);
				}
				logger.debug("using connection '" + defaultDataSourceName
						+ " from environment "
						+ DbexpertsEnvironment.DEXTEROUS_DATA_SOURCE);
			}
		}
		if (conn == null) {
			String message = "no connection has been specified for database operation on line: "
					+ parser.getLineNumber();
			if (getCurrentFile() != null) {
				message = message + "of file "
						+ getCurrentFile().getAbsolutePath();
			}

			logger.error(message);
			throw new DexterousStateException(message);
		}
		return conn;
	}

	// /* (non-Javadoc)
	// * @see org.javautil.dex.dexterous.DexterousState#write(java.lang.String)
	// */
	// public void write(String text) throws IOException {
	// if (writers.size() == 0) {
	// addDefaultWriter();
	// }
	// writers.write(text);
	//
	// }

	@Override
	public boolean hasRequestFiles() {
		return requestFiles != null && requestFiles.size() > 0;
	}

	@Override
	public Collection<File> getRequestFiles() {
		return requestFiles;
	}

	//
	// public void setBindValues(HashMap<String, BindVariableValue> bindValues)
	// {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void setRequestFiles(final Collection<File> inputFiles) {
		this.requestFiles = inputFiles;

	}

	@Override
	public void setVerifyOnly(final boolean verifyOnly) {

	}

	@Override
	public boolean sqlIsSelect() {
		return sqlIsSelect;
	}

	@Override
	public SimpleDateFormat getDateFormatter() {
		return dateFormatter;
	}

	@Override
	public Element getCurrentElement() {
		// TODO Auto-generated method stub
		return currentElement;
	}

	@Override
	public String getHtmlElementName() {
		// TODO Auto-generated method stub
		return htmlElementName;
	}

	@Override
	public void setLastStringStores(final StringStores stringStores) {
		// todo implement
	}

	public Map<String, BindVariable> getBindVariables() {
		// todo implement
		return null;
	}

	@Override
	public Document getXmlDocument() {
		// todo implement
		return null;
	}

	@Override
	public ExcelXmlWriter getXmlWorkbook() {
		return xmlWorkbook;
	}

	@Override
	public boolean isEmitXmlAsElement() {
		return emitXmlAsElement;
	}

	@Override
	public boolean isCrossTab() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ExcelXmlWriter getExcelXmlWriter() {
		return excelXmlWriter;
	}

	@Override
	public int getMaxSqlExceptionCount() {
		return maxSqlExceptionCount;
	}

	@Override
	public int getSqlExceptionCount() {
		return sqlExceptionCount;
	}

	@Override
	public void incrementSqlExceptionCount() {
		sqlExceptionCount++;
	}

	@Override
	public void setExcelXmlWriter(final ExcelXmlWriter excelXmlWriter) {
		this.excelXmlWriter = excelXmlWriter;
	}

	public boolean isCrosstab() {
		boolean retval = false;
		if (crossTabColumns != null || crossTabRows != null
				|| crossTabValues != null) {
			if (crossTabColumns == null || crossTabRows == null
					|| crossTabValues == null) {
				final StringBuilder m = new StringBuilder();
				m.append("crossTabColumns");
				m.append(crossTabColumns == null ? "is null" : "specified");
				m.append("crossTabRows ");
				m.append(crossTabRows == null ? " is null" : " specified");
				m.append("crossTabValues ");
				m.append(crossTabValues == null ? " is null" : " specified");

				throw new IllegalArgumentException(
						"inconsistent crosstab state " + m.toString());
			}
			retval = true;
		}
		return retval;
	}

	@Override
	public String getLastStringStores() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getStore(final String upperCase) {
		return stringStores.getStore(upperCase);

	}

	@Override
	public long getRowCount() {
		return rowCount;
	}

	@Override
	public void setParser(final Parser parser) {
		this.parser = parser;
	}

	@Override
	public String getSelectText() {

		return sqlIsSelect() ? getSqlText() : null;

	}

	@Override
	public String getStateInfo() {
		final StringBuilder b = new StringBuilder();
		if (parser.getCommandType().isSQL()) {
			b.append(" line # " + parser.getLineNumber() + newline
					+ getSqlText() + newline);
		} else {
			b.append(" line # " + parser.getLineNumber() + " "
					+ parser.getCurrentLine() + newline);
		}
		b.append("last command type " + parser.getCommandType());
		return b.toString();
	}

	@Override
	public File getLogDirectory() {
		throw new UnsupportedOperationException();
		// return null;
	}

	@Override
	public boolean isInteractive() {
		boolean retval = requestFiles.size() == 0;
		return retval;
	}

	/**
	 * Constructs a <code>String</code> with all attributes in name = value
	 * format.
	 * 
	 * @return a <code>String</code> representation of this object.
	 */
	@Override
	public String toString() {
		final String TAB = System.getProperty("line.separator");

		String retValue = "State ( "
				+ super.toString()
				+ TAB
				+ "allowableQueryMilliseconds = "
				+ ((this.allowableQueryMilliseconds == null) ? "null" : ("'"
						+ this.allowableQueryMilliseconds + "'"))
				+ TAB
				+ "autoTrace = "
				+ autoTrace
				+ TAB
				+ "baseDirectoryName = "
				+ ((this.baseDirectoryName == null) ? "null" : ("'"
						+ this.baseDirectoryName + "'"))
				+ TAB
				+ "bindValues = "
				+ ((this.bindValues == null) ? "null"
						: ("'" + this.bindValues + "'"))
				+ TAB
				+ "bindVariables = "
				+ ((this.bindVariables == null) ? "null" : ("'"
						+ this.bindVariables + "'"))
				+ TAB
				+ "breaks = "
				+ ((this.breaks == null) ? "null" : ("'" + this.breaks + "'"))
				+ TAB
				+ "closeWriters = "
				+ this.closeWriters
				+ TAB
				+ "cm = "
				+ ((this.cm == null) ? "null" : ("'" + this.cm + "'"))
				+ TAB
				+ "conn = "
				+ ((this.conn == null) ? "null" : ("'" + this.conn + "'"))
				+ TAB
				+ "consoleAppender = "
				+ ((this.consoleAppender == null) ? "null" : ("'"
						+ this.consoleAppender + "'"))
				+ TAB
				+ "crossTabColumns = "
				+ ((this.crossTabColumns == null) ? "null" : ("'"
						+ this.crossTabColumns + "'"))
				+ TAB
				+ "crossTabRows = "
				+ ((this.crossTabRows == null) ? "null" : ("'"
						+ this.crossTabRows + "'"))
				+ TAB
				+ "crossTabValues = "
				+ ((this.crossTabValues == null) ? "null" : ("'"
						+ this.crossTabValues + "'"))
				+ TAB
				+ "currentElement = "
				+ ((this.currentElement == null) ? "null" : ("'"
						+ this.currentElement + "'"))
				+ TAB
				+ "currentFile = "
				+ ((this.currentFile == null) ? "null" : ("'"
						+ this.currentFile + "'"))
				+ TAB
				+ "dataSource = "
				+ ((this.dataSource == null) ? "null"
						: ("'" + this.dataSource + "'"))
				+ TAB
				+ "dataSources = "
				+ ((this.dataSources == null) ? "null" : ("'"
						+ this.dataSources + "'"))
				+ TAB
				+ "dateFormat = "
				+ ((this.dateFormat == null) ? "null"
						: ("'" + this.dateFormat + "'"))
				+ TAB
				+ "dateFormatter = "
				+ ((this.dateFormatter == null) ? "null" : ("'"
						+ this.dateFormatter + "'"))
				+ TAB
				+ "defaultDataSourceName = "
				+ ((this.defaultDataSourceName == null) ? "null" : ("'"
						+ this.defaultDataSourceName + "'"))
				+ TAB
				+ "dexterLogFileName = "
				+ ((this.dexterLogFileName == null) ? "null" : ("'"
						+ this.dexterLogFileName + "'"))
				+ TAB
				+ "dexterLogWriter = "
				+ ((this.dexterLogWriter == null) ? "null" : ("'"
						+ this.dexterLogWriter + "'"))
				+ TAB
				+ "document = "
				+ ((this.document == null) ? "null"
						: ("'" + this.document + "'"))
				+ TAB
				+ "documentTemplate = "
				+ ((this.documentTemplate == null) ? "null" : ("'"
						+ this.documentTemplate + "'"))
				+ TAB
				+ "echo = "
				+ this.echo
				+ TAB
				+ "emitMetaData = "
				+ this.emitMetaData
				+ TAB
				+ "emitXmlAsElement = "
				+ this.emitXmlAsElement
				+ TAB
				+ "endOfInput = "
				+ this.endOfInput
				+ TAB
				+ "events = "
				+ ((this.events == null) ? "null" : ("'" + this.events + "'"))
				+ TAB
				+ "excelXmlWriter = "
				+ ((this.excelXmlWriter == null) ? "null" : ("'"
						+ this.excelXmlWriter + "'"))
				+ TAB
				+ "fileDateFormatter = "
				+ ((this.fileDateFormatter == null) ? "null" : ("'"
						+ this.fileDateFormatter + "'"))
				+ TAB
				+ "fileStack = "
				+ ((this.fileStack == null) ? "null"
						: ("'" + this.fileStack + "'"))
				+ TAB
				+ "htmlElementName = "
				+ ((this.htmlElementName == null) ? "null" : ("'"
						+ this.htmlElementName + "'"))
				+ TAB
				+ "logDirectory = "
				+ ((this.logDirectory == null) ? "null" : ("'"
						+ this.logDirectory + "'"))
				+ TAB
				+ "logDirectoryName = "
				+ ((this.logDirectoryName == null) ? "null" : ("'"
						+ this.logDirectoryName + "'"))
				+ TAB
				+ "logger = "
				+ ((this.logger == null) ? "null" : ("'" + this.logger + "'"))
				+ TAB
				+ "makePaths = "
				+ this.makePaths
				+ TAB
				+ "maxSqlExceptionCount = "
				+ this.maxSqlExceptionCount
				+ TAB
				+ "mimeType = "
				+ ((this.mimeType == null) ? "null"
						: ("'" + this.mimeType + "'"))
				+ TAB
				+ "namespace = "
				+ ((this.namespace == null) ? "null"
						: ("'" + this.namespace + "'"))
				+ TAB
				+ "parser = "
				+ ((this.parser == null) ? "null" : ("'" + this.parser + "'"))
				+ TAB
				+ "reader = "
				+ ((this.reader == null) ? "null" : ("'" + this.reader + "'"))
				+ TAB
				+ "requestFiles = "
				+ ((this.requestFiles == null) ? "null" : ("'"
						+ this.requestFiles + "'"))
				+ TAB
				+ "reservedWords = "
				+ ((this.reservedWords == null) ? "null" : ("'"
						+ this.reservedWords + "'"))
				+ TAB
				+ "resultSetId = "
				+ ((this.resultSetId == null) ? "null" : ("'"
						+ this.resultSetId + "'"))
				+ TAB
				+ "rowCount = "
				+ this.rowCount
				+ TAB
				+ "sqlExceptionCount = "
				+ sqlExceptionCount
				+ TAB
				+ "sqlIsSelect = "
				+ this.sqlIsSelect
				+ TAB
				+ "sqlText = "
				+ ((this.sqlText == null) ? "null" : ("'" + this.sqlText + "'"))
				+ TAB
				+ "stringStores = "
				+ ((this.stringStores == null) ? "null" : ("'"
						+ this.stringStores + "'"))
				+ TAB
				+ "stylesheetFileName = "
				+ ((this.stylesheetFileName == null) ? "null" : ("'"
						+ this.stylesheetFileName + "'"))
				+ TAB
				+ "termout = "
				+ this.termout
				+ TAB
				+ "traceFileName = "
				+ ((this.traceFileName == null) ? "null" : ("'"
						+ this.traceFileName + "'"))
				+ TAB
				+ "traceId = "
				+ ((this.traceId == null) ? "null" : ("'" + this.traceId + "'"))
				+ TAB
				+ "traceLogger = "
				+ ((this.traceLogger == null) ? "null" : ("'"
						+ this.traceLogger + "'"))
				+ TAB
				+ "useCacheThisRequest = "
				+ useCacheThisRequest
				+ TAB
				+ "useCachedIfAvailable = "
				+ useCachedIfAvailable
				+ TAB
				+ "workbook = "
				+ ((this.workbook == null) ? "null"
						: ("'" + this.workbook + "'"))
				+ TAB
				+ "worksheetName = "
				+ ((this.worksheetName == null) ? "null" : ("'"
						+ this.worksheetName + "'"))
				+ TAB
				+ "writers = "
				+ ((this.writers == null) ? "null" : ("'" + this.writers + "'"))
				+ TAB
				+ "xmlWorkbook = "
				+ ((this.xmlWorkbook == null) ? "null" : ("'"
						+ this.xmlWorkbook + "'")) + TAB + " )";

		return retValue;
	}

	@Override
	public boolean isConnected() {
		return conn != null;
	}

}
