package org.javautil.dex.dexterous;

//@TODO write regression tests
//@TODO show how to get dense dates on crosstab
//@TODO command line dexterous no longer puts out the xml header
//@TODO dexterous2 report no longer transforms
//@TODO write only to WriterSet and Switch the writer set based on whether a transform is occurring
// TODO don't blow up if interactive and can't get data source
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import javax.servlet.ServletException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.javautil.dex.DbexpertsEnvironment;
import org.javautil.dex.Dexter;
import org.javautil.dex.DexterousException;
import org.javautil.dex.DexterousIOException;
import org.javautil.dex.DexterousStateException;
import org.javautil.dex.JdbcHelper;
import org.javautil.dex.arguments.DexterousArguments;
import org.javautil.dex.bsh.Cursor;
import org.javautil.dex.parser.DexterousEvents;
import org.javautil.dex.parser.NodeRelationship;
import org.javautil.dex.parser.Parser;
import org.javautil.dex.renderer.RendererManager;
import org.javautil.dex.renderer.interfaces.Renderer;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.dex.servlet.DexterousServletHelper;
import org.javautil.document.MimeType;
import org.javautil.file.FileHelper;
import org.javautil.io.FilePathHelper;
import org.javautil.io.ResourceText;
import org.javautil.io.WriterSet;
import org.javautil.io.WriterWrapper;
import org.javautil.jdbc.BindVariableValue;
import org.javautil.jdbc.SelectHelper;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.InvalidDataSourceException;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;
import org.javautil.logging.Events;
import org.javautil.persistence.PersistenceException;
import org.javautil.xml.TransformerHelper;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.UtilEvalError;
import bsh.classpath.ClassManagerImpl;

//...QueryHelper

/**
 * Enhancement history
 * 	2007-08-04 Added support for <% script delimiters
 *             Added show waits
 *             Added show stats
 *             Added spool to Dexterous in order to support
 */
/**
 * TODO add verify mode that just checks that the script works TODO check why
 * statement on connection is not working TODO add verbosity levels for use in
 * verify and for checking progress @ todo if a sql file is specified from the
 * command line, caching should not TODO set file log level FINE TODO set log
 * directory DIRECTORY_NAME TODO set stdout log level FINE TODO config sticky on
 * connection dexterous property under connections and per connection TODO
 * writeln writes to spool file valid on what mimes? TODO support dexterous
 * command in connection TODO support security scripts for authentication and
 * rls TODO support dexterous function declaration and invocation TODO get a
 * separate connection for getting waits and stats
 * 
 * 
 * @TODO trap sql exception and wrap
 * @author jim
 * 
 */

public class Dexterous {
	public static final String revision = "$Revision: 1.1 $";

	public static final long WRITE_PROMPTS = 1;

	public static final long WRITE_ALL = Long.MAX_VALUE;

	// private static final String BSH_INIT_SCRIPT =
	// "classpath:org.javautil.dex/bsh.rc";
	private static final String BSH_INIT_SCRIPT = "classpath:bsh.rc";
	private static String newline = System.getProperty("line.separator");

	// private static EventHelper events = new EventHelper();

	/**
	 * xml root element name
	 */
	public static final String ROOT_ELEMENT_NAME = "document";

	private static Events traceEvents = new Events();

	private DexterousState state = new State();

	private Logger logger = Logger.getLogger(getClass().getName());

	private Dexter dexter;

	private final WriterSet writers = new WriterSet("dexterous default");

	// private boolean endOfInput = false;

	private final boolean zipCache = false;

	private DexterousArguments arguments = new DexterousArguments();

	private Parser parser;

	private File logFile = null;

	private final Stack<FileState> fileStack = new Stack<FileState>();

	private final Interpreter bsh = new Interpreter();

	private final ClassManagerImpl cm = new ClassManagerImpl();

	private final NameSpace namespace = new NameSpace(cm, "dexterous");

	private boolean parseUntilGo;

	private ConsoleAppender console;

	private RendererManager rendererManager;

	private final boolean dumpState = true;

	static {
		// traceEvents.addEvent(LOG_SQL);
		// traceEvents.addEvent(LOG_WRITER_DESCRIPTIONS);
		// traceEvents.addEvent(FILE_CLOSES);
		// traceEvents.addEvent(Events.STACK_TRACE_ON_SQLEXCEPTION);
		// events.addEvent(STACK_TRACE_ON_SQLEXCEPTION);

	}

	public void flushPendingWrites() throws IOException, TransformerException {
		writers.close();
		if (state.getXmlDocument() != null) {
			writeDocument();
		}
		if (state.getWorkbook() != null) {
			writeWorkbooks();
		}
	}

	/**
	 * TODO add EXCEPTION NONEXISTANT_DIRECTORY makepath
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String args[]) throws Exception {
		final Dexterous dexterous = new Dexterous();
		try {
			// was getting double loggers when called from Main
			// LoggingConfigurator.configure();
			final DexterousArguments parsedArgs = new DexterousArguments();
			// dexterous.logger.debug("parsed ");
			dexterous.processEnvironment();
			parsedArgs.processArguments(args);
			dexterous.setArguments(parsedArgs);
			// dexterous.startLogging();
			dexterous.state.setRequestFiles(parsedArgs.getInputFiles());
			dexterous.state.setDataSourceName(parsedArgs.getDataSourceName());

			DataSources dataSources = new SimpleDatasourcesFactory(parsedArgs
					.getConfigFile().getAbsolutePath());
			dexterous.state.setDataSources(dataSources);

			if (dexterous.state.hasRequestFiles()) {
				// dexterous.stdoutPrompt = null;
				dexterous.processFiles();
			} else {
				final LineNumberReader r = new LineNumberReader(
						new InputStreamReader(System.in));
				dexterous.state.setInputReader(r);

				dexterous.parser = new Parser(dexterous, dexterous.state);
				dexterous.parser.setStdOutPrompt("dex>");
				dexterous.process();

			}
			if (dexterous.state.isConnected()) {
				dexterous.state.getConnection().close();
			}
			dexterous.flushPendingWrites();

		} catch (final SQLException sqe) {
			// if
			// (dexterous.state.getEvents().exists(Events.STACK_TRACE_ON_SQLEXCEPTION))
			// {
			// sqe.printStackTrace();
			// throw sqe;
			// }
			dexterous.logger.error(sqe.getMessage());
		} catch (final Exception e) {
			e.printStackTrace();
			throw e;
		}

	}

	private void processEnvironment() {
		final String baseDir = System
				.getenv(DbexpertsEnvironment.BASE_DIRECTORY);
		if (baseDir != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("setting baseDirectory to " + baseDir
						+ " from environment variable "
						+ DbexpertsEnvironment.BASE_DIRECTORY);
			}
			state.setBaseDirectoryName(baseDir);
		}

	}

	public Dexterous() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		initBsh();
		registerRenderers();
	}

	public Dexterous(final DexterousState state) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		this.state = state;
		initBsh();
		registerRenderers();
	}

	public Cursor createCursor(final String cursorName, final String mySqlText,
			final String recordName) throws UtilEvalError,
			DexterousStateException, PersistenceException {

		final Cursor c = new Cursor(state.getConnection(), cursorName,
				mySqlText, namespace, recordName);
		namespace.setVariable(cursorName, c, false);
		return c;
	}

	// /**
	// *
	// */
	// public void endOfInput() {
	// endOfInput = true;
	// }

	public void evaluate(final String script) throws DexterousException {
		// @TODO push all the variables
		// StringReader r = new StringReader(script);
		state.setParseUntilGo(true);
		try {
			bsh.eval(script, namespace);
		} catch (final EvalError e1) {
			throw new DexterousException(e1);
		}
		for (final String name : namespace.getAllNames()) {
			Object o;
			try {
				o = namespace.getVariable(name);

			} catch (final UtilEvalError e) {
				logger.error("not likely since we were enumerating");
				throw new IllegalStateException(e.getMessage()
						+ " while getting '" + name + "'");
			}
			if (logger.isDebugEnabled()) {
				logger.debug(name + ":" + o.toString() + " "
						+ o.getClass().getName());
			}
		}
		setBindVariablesFromNamespace();

	}

	/**
	 * @TODO collapse with generateSelect
	 * @param tableName
	 * @throws SQLException
	 * @throws DexterousStateException
	 * @throws InvalidDataSourceException
	 */
	public void generateJdbcSelect(final String tableName) throws SQLException,
			InvalidDataSourceException, DexterousStateException {
		JdbcHelper.generateJdbcSelect(state.getConnection(), tableName);
	}

	public void generateSelect(final String tableName) throws SQLException,
			InvalidDataSourceException, DexterousStateException {
		JdbcHelper.generateSelect(state.getConnection(), tableName);
	}

	public void process() throws DexterousException, SQLException,
			DexterousStateException, IOException, RenderingException,
			InstantiationException, IllegalAccessException,
			TransformerException {
		// public void process() throws IOException, SQLException,
		// TransformerException, DexterousException, RenderingException,
		// InvalidDataSourceException, DexterousStateException,
		// InstantiationException, IllegalAccessException {

		System.currentTimeMillis();

		// rowCount = -1;
		dexter = new Dexter();
		// TODO reintroduce caching management
		while (!state.isEndOfInput()) {
			try {
				processRequests();
			} catch (InvalidDataSourceException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (DexterousException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (SQLException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (DexterousStateException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (IOException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (RenderingException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (InstantiationException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			} catch (IllegalAccessException e) {
				if (state.isInteractive()) {
					logger.error(e.getMessage());
				} else {
					throw e;
				}
			}
		}

		// all document writes should be together
		closeInput();
		try {
			writeOutput();
		} catch (TransformerException e) {
			if (state.isInteractive()) {
				logger.error(e.getMessage());
			} else {
				throw e;
			}
		}
		closeOutput();
		logger.debug("done");
	}

	void processRequests() throws DexterousException, SQLException,
			InvalidDataSourceException, DexterousStateException, IOException,
			RenderingException, InstantiationException, IllegalAccessException {
		while (!state.isEndOfInput()) {

			parser.parseRequest();
			if (dumpState) {
				logger.info(newline + state.toString());
			}
			addDefaultWriter();

			if (state.getSqlText() != null && !state.sqlIsSelect()
					&& !parseUntilGo) {
				executeSql();
			} else {
				if (state.getSelectText() != null) {
					processRequest();
					// callDexter();
					// tb need to determine whether to recover or terminate
				} else {
					logger.debug("no sql to process");
				}
				// selectPending = false;
				state.setSqlText(null);
			}
		}
	}

	private void writeOutput() throws IOException, TransformerException {

		switch (state.getMimeType()) {
		case EXCEL:
			// TODO this is expensive need an OutputStreamSet
			for (final WriterWrapper writer : writers.getWriters()) {
				state.getWorkbook().write(writer.getWriter());
				writer.flush();
			}
			break;
		case XML:
			writeDocument();
			break;
		default:
			logger.debug("not writing output, should have been handled by dexter");
		}
	}

	public void processFiles() throws IOException, SQLException,
			TransformerException, DexterousException, RenderingException,
			InvalidDataSourceException, DexterousStateException,
			InstantiationException, IllegalAccessException {
		for (final File file : state.getRequestFiles()) {
			// set state.endOfInputFalse ??
			setCurrentFile(file);
			process();
		}
		state.setEndOfInput(true);
		flushAndCloseWriters();
	}

	public void setArguments(final DexterousArguments args) {

		arguments = args;
		state.setRequestFiles(args.getInputFiles());
		// stdoutPrompt = args.getPrompt();

		state.setMimeType(args.getDefaultMimeType());
		state.setDefaultDataSourceName(args.getDataSourceName());
		state.setVerifyOnly(args.isVerifyOnly());
		state.addEvents(args.getEvents());

	}

	/**
	 * Sets the sql bind variables from the variables in the the bsh namespace.
	 * 
	 * @TODO should use a single namespace in the future.
	 * 
	 */
	// @TODO check for lower case variable names and name case collision
	public void setBindVariablesFromNamespace() {
		for (final String name : namespace.getAllNames()) {
			Object o;
			try {
				o = namespace.getVariable(name);
				if (!(o instanceof bsh.Primitive)) {
					final BindVariableValue bvv = new BindVariableValue(
							name.toUpperCase(), o);
					state.getBindValues().put(name.toUpperCase(), bvv);
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

	//
	// /**
	// * @param endOfInput
	// * the endOfInput to set
	// */
	// public void setEndOfInput(final boolean endOfInput) {
	// this.endOfInput = endOfInput;
	// }

	public void setLogLevel(final Level level) {
		logger.setLevel(level);
	}

	public void setReader(final Reader reader) {
		// Reader reader = new LineNumberReader(reader);
		final LineNumberReader lnr = new LineNumberReader(reader);
		state.setInputReader(lnr);
		parser = new Parser(this, state);
	}

	//
	// /**
	// * @param resultSetId
	// * the resultSetId to set
	// */
	// public void setResultSetId(String resultSetId) {
	// this.resultSetId = resultSetId;
	// }

	public void setCurrentFile(final File sqlFile) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("about to process " + sqlFile.getCanonicalPath());
		}
		state.setRequestFile(sqlFile);
		final LineNumberReader reader = new LineNumberReader(
				new BufferedReader(new FileReader(sqlFile)));
		state.setInputReader(reader);
		parser = new Parser(this, state);
	}

	public void spool(String fileName) throws IOException {
		if (fileName.equalsIgnoreCase("off")) {
			clearNonStdoutWriters();

		} else {
			if (fileName.contains("%d")) {
				final SimpleDateFormat sdf = state.getFileDateFormatter();
				final String dateText = sdf.format(new Date());
				final String newFileName = fileName.replaceAll("%d", dateText);
				logger.info("changed file name from '" + fileName + "' to '"
						+ newFileName);
				fileName = newFileName;
			}
			// closeReplicatorOutput();
			File file = new File(fileName);
			final String extension = FilePathHelper.getFileExtension(file);
			String newFileName = null;
			if (extension == null || extension.length() == 0) {
				if (MimeType.EXCEL.equals(state.getMimeType())) {
					newFileName = fileName + ".xls";
				} else {
					newFileName = fileName + "."
							+ state.getMimeType().toString().toLowerCase();
				}
			} else {
				newFileName = fileName;
			}
			if (state.getBaseDirectoryName() != null) {
				logger.info("prepending base directory ");
				newFileName = state.getBaseDirectoryName() + "/" + newFileName;
			}
			if (MimeType.EXCEL.equals(state.getMimeType())) {
				// TODO do in Workbook manager
				state.setWorkbook(new HSSFWorkbook());
			}
			// String message = "";
			file = new File(newFileName);
			logger.debug("writing to " + file.getAbsolutePath());
			logger.debug("makePaths " + state.isMakePaths());
			if (state.isMakePaths()) {
				FileHelper.makePathForFileName(newFileName);
			}
			final OutputStream os = new FileOutputStream(newFileName);
			clearNonStdoutWriters();

			addWriter(os, newFileName, true);
		}
	}

	// private void startXmlDocument() {
	// if (document == null) {
	// document = DocumentHelper.createDocument();
	// document.addElement("document");
	// } else {
	// throw new
	// IllegalStateException("document is not null and request to create anew");
	// }
	// }

	// @TODO move to generic
	private void executeSql() throws SQLException, InvalidDataSourceException,
			DexterousStateException {
		final PreparedStatement ps = state.getConnection().prepareStatement(
				state.getSqlText());
		try {
			ps.executeUpdate();
		} catch (final SQLException sqe) {
			state.incrementSqlExceptionCount();
			final int lineNumber = parser.getLineNumber();
			final String message = "SQL Exception count is: "
					+ state.getSqlExceptionCount() + " max allowed is: "
					+ state.getMaxSqlExceptionCount();
			logger.error("while processing:\n " + state.getSqlText() + newline
					+ sqe.getMessage() + newline + message + newline
					+ "occurred while processing line# " + lineNumber);

			if (state.getSqlExceptionCount() >= state.getMaxSqlExceptionCount()) {
				throw new IllegalStateException("giving up.... " + message);
			}

		}
		ps.close();
	}

	// private String getDocumentElementNameClose() {
	//
	// return "</" + documentElementName + ">" + EOL;
	// }

	// @TODO create ability load the initialization script as a resource and
	// from an environment variable
	private void initBsh() {

		try {
			// final Reader r = StringHelper.getResourceReader(this,
			// BSH_INIT_SCRIPT);
			final Reader r = new ResourceText()
					.getResourceReader(BSH_INIT_SCRIPT);
			namespace.setVariable("dexterous", this, false);
			bsh.set("dexterous", this);
			bsh.eval(r, namespace, "initialization script");
			r.close();
		} catch (final Exception e) {
			String message = null;
			if (e.getCause() == null) {
				message = e.getMessage();
			} else {
				message = e.getMessage() + " cause "
						+ e.getCause().getMessage() + " type "
						+ e.getClass().getName();
			}
			throw new java.lang.IllegalStateException(message);
		}

	}

	//
	// Oracle stuff
	//
	public void showStats() throws SQLException, DexterousStateException {
		// TODO restore
		// OracleSessionStatistics.showStats(state.getConnection());

	}

	public String describe(final String dbObjectName) throws SQLException,
			DexterousStateException {
		return JdbcHelper.describe(state.getConnection(), dbObjectName);

	}

	public void showWait() throws SQLException, DexterousStateException {
		// TODO restore
		// OracleSessionStatistics.showWait(state.getConnection());

	}

	public void popFile() {
		if (!fileStack.empty()) {
			final FileState fs = fileStack.pop();
			state.setRequestFile(fs.getRequestFile());
			state.setInputReader(fs.getReader());
			parser = fs.getParser();
		} else {
			state.setEndOfInput(true);
		}
	}

	public void pushFile(final File file) throws IOException, SQLException,
			TransformerException, DexterousException, RenderingException,
			InvalidDataSourceException, DexterousStateException,
			InstantiationException, IllegalAccessException {
		final FileState fs = new FileState(state.getCurrentFile(),
				state.getInputReader(), parser);
		fileStack.push(fs);
		state.setEndOfInput(false);
		setCurrentFile(file);
		process();
	}

	// private void startLogging() throws SecurityException {
	//
	// console = new ConsoleAppender();
	// logger = Logger.getLogger(this.getClass().getName());
	//
	// final Level stdoutLevel = arguments.getStdoutLoggerLevel();
	// Logger.getLogger("").setLevel(stdoutLevel);
	// logger.info("log level " + stdoutLevel);
	//
	// logger.info("Dexterous version " + getVersion());
	// }

	//
	//
	// IO Section
	//

	private void writeDocument() throws IOException, TransformerException {
		if (state.getStylesheetFileName() == null) {
			logger.info("writing  document " + writers.getWriterDescriptions());
			final XMLWriter xw = new XMLWriter(writers,
					OutputFormat.createPrettyPrint());
			xw.write(state.getXmlDocument());
			xw.flush();
			xw.close();
		} else {
			writeTransformedXml(writers);
			writers.close();
		}

	}

	//
	// closes
	//

	public void closeInput() throws IOException {
		if (state.getInputReader() != null) {
			state.getInputReader().close();
		}
	}

	public void closeOutput() throws IOException {
		// if (!state.hasRequestFiles()) {
		// logger.debug("flushing and closing writers");
		// flushAndCloseWriters();
		// } else {
		// logger.debug("not closing writers because processing a list of files");
		// }
		flushAndCloseWriters();
	}

	/**
	 * @throws IOException
	 */
	public void closeWriters() throws IOException {
		writers.close();
	}

	public void write(final String text) throws IOException {
		if (writers.size() == 0) {
			addDefaultWriter();
		}
		writers.write(text);

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

	void addWriter(final OutputStream os, final String newFileName,
			final boolean pretty) {
		writers.addWriter(os, newFileName, pretty);
	}

	//
	// need add a writerset thread
	//
	public void writeWorkbooks() throws IOException {
		logger.debug("writing workbooks");
		if (state.getMimeType().equals(MimeType.EXCEL)) {
			if (writers.size() > 1) {

				logger.warn("Writing workbook to multiple writers");
			}
			// TODO write a writers getMultiplexStream
			for (final WriterWrapper writer : writers.getWriters()) {
				// TODO write workbook manager
				state.getWorkbook().write(writer.getWriter());
			}
		}
		writers.close();

	}

	/**
	 * If no writers have been specified create a default writer as stdout.
	 * 
	 * If the output is binary and no writer has been added throws
	 * IllegalStateException.
	 * 
	 * @throws DexterousStateException
	 * 
	 * @throws IllegalStateException
	 */
	private void addDefaultWriter() throws DexterousIOException {

		if (writers.size() == 0) {
			if (state.getMimeType().equals(MimeType.EXCEL)) {
				throw new DexterousIOException(
						"binary output with no destination from writers "
								+ writers.getName());
			}
			writers.addWriter(System.out, "stdout", true, WRITE_PROMPTS);
		}
		if (Events.isRegistered(DexterousEvents.LOG_WRITER_DESCRIPTIONS)
				&& state.getTraceLogger() != null) {
			state.getTraceLogger().debug(state.getWriterDescriptions());
		}
	}

	//
	//
	// Writer routines
	//
	//

	private void flushAndCloseWriters() throws IOException {

		if (state.getExcelXmlWriter() != null) {
			state.getExcelXmlWriter().closeWorkbook();
			state.setExcelXmlWriter(null);
		} else {
			closeWriters();
		}
		// if (dexterLogWriter != null) {
		// dexterLogWriter.close();
		// }
	}

	private void writeTransformedXml(final Writer writer)
			throws TransformerException {

		final TransformerHelper th = new TransformerHelper();
		th.setDocumentSource(state.getXmlDocument());
		th.setTransformer(new File(state.getStylesheetFileName()));
		th.writeTransformedXml(writer);
	}

	public File getLogFile() {
		if (logFile == null) {
			final File logDir = arguments.getLogDirectory();

			final String logMask = arguments.getLogFileNameMask();
			String fileName = null;
			if (logMask != null) {
				fileName = logMask.replaceAll("%f", state.getCurrentFile()
						.getName());
				if (fileName.contains("%d")) {
					final SimpleDateFormat sdf = state.getFileDateFormatter();
					final String dateText = sdf.format(new Date());
					fileName = fileName.replaceAll("%d", dateText);
					logger.info("changed file name from '" + logMask + "' to '"
							+ fileName);
				}
			} else {
				fileName = state.getCurrentFile().getName() + ".log.xml";
			}
			final String logFileName = logDir + "/" + fileName;
			logFile = new File(logFileName);
			if (logFile.exists() && !logFile.canWrite()) {
				if (logger == null) {
					logger = Logger.getLogger(this.getClass().getName());
				}
				logger.error("cannot write to '" + logFile.getAbsolutePath()
						+ " directory: " + logDir.getAbsolutePath()
						+ "' file:  '" + logFileName + "'");

			}
			if (!logDir.exists()) {
				logger.error("directory " + logDir.getAbsolutePath()
						+ " does not exist");
			} else if (!logDir.canWrite()) {
				logger.error("cannot write to directory "
						+ logDir.getAbsolutePath());
			}
		}
		return logFile;
	}

	// poorly named writes the store to
	// public void getStore(String columnName) throws IOException {
	//
	// if (columnName == null) {
	// throw new IllegalArgumentException("columnName is null");
	// }
	// Collection<String> values = state.getStore(columnName.toUpperCase());
	// if (values == null) {
	// throw new IllegalArgumentException("no such store " + columnName);
	// }
	//
	// Element store = DocumentHelper.createElement("store");
	// store.addAttribute("name", columnName);
	// for (String value : values) {
	// store.addElement("d").addAttribute("v", value);
	// }
	// XMLWriter xw = new XMLWriter(writers, OutputFormat.createPrettyPrint());
	// xw.write(store);
	// xw.flush();
	// writers.flush();
	//
	// }

	public void go() throws SQLException, IOException, DexterousStateException,
			RenderingException, InvalidDataSourceException,
			InstantiationException, IllegalAccessException {
		setBindVariablesFromNamespace();
		addDefaultWriter();
		processRequest();
	}

	/**
	 * This should only be called from servlets.
	 * 
	 * @param sh
	 * @throws ServletException
	 */
	public void setServletHelper(final DexterousServletHelper sh)
			throws ServletException {
		// sh.setDexterous(this);
		try {
			sh.process();

		} catch (final Exception e) {
			e.printStackTrace();
			final String message = e.getClass().getName() + e.getMessage();
			logger.error(message);
			throw new ServletException(message);
		}
	}

	public void write() throws IOException, TransformerException {
		if (state.getMimeType().equals(MimeType.XML)) {
			writeOutput();
		} else {
			logger.error("no reason to writeOutput");
		}
	}

	public void setDocumentTemplateXpath(final String xpath,
			final NodeRelationship nodeRelationship, final boolean asElement) {
		// TODO Auto-generated method stub

	}

	private void processRequest() throws RenderingException,
			InstantiationException, IllegalAccessException, IOException,
			SQLException, InvalidDataSourceException, DexterousStateException {
		final Renderer renderer = rendererManager.getRenderer(state);

		final SelectHelper selectHelper = new SelectHelper(
				state.getConnection(), state.getSqlText());
		selectHelper.setAllowUnbound(true);
		selectHelper.setMaxQuerySec(state.getAllowableQueryMilliseconds());
		// selectHelper.setExecuteQueryTimeoutMilliseconds(allowableQueryMilliseconds);
		if (state.getBindValues() != null && state.getBindValues().size() > 0) {
			selectHelper.setBindVariableValues(state.getBindValues().values());
		}
		renderer.setResultSet(selectHelper.getResultSet());
		renderer.process();

		// rowCount = selectHelper.getRowCount();
		selectHelper.dispose();
	}

	@SuppressWarnings("unchecked")
	private void registerRenderers() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		if (rendererManager == null) {
			final RendererManager mgr = rendererManager = new RendererManager();
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.CsvCrosstabWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.CsvWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.ExcelCrosstabWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.ExcelListWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.ExcelXmlListWriter"));
			// mgr.registerRenderer((Class<Renderer>)Class.forName("org.javautil.dex.renderer.ExcelXmlWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.FixedWidthWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.HtmlCrossTabWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.HtmlSelectWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.HtmlWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.XhtmlCrossTabWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.XmlChartWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.XmlCrossTabWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.XmlTypeWriter"));
			mgr.registerRenderer((Class<Renderer>) Class
					.forName("org.javautil.dex.renderer.XmlWriter"));

		}
	}
}
