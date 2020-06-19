package org.javautil.dex.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.javautil.dex.CursorBreakScripts;
import org.javautil.dex.DexterousException;
import org.javautil.dex.DexterousStateException;
import org.javautil.dex.bsh.Cursor;
import org.javautil.dex.dexterous.Dexterous;
import org.javautil.dex.dexterous.DexterousCommand;
import org.javautil.dex.dexterous.DexterousState;
import org.javautil.dex.parser.command.BeanShellScript;
import org.javautil.dex.parser.command.Break;
import org.javautil.dex.parser.command.Command;
import org.javautil.dex.parser.command.Commit;
import org.javautil.dex.parser.command.DML;
import org.javautil.dex.parser.command.DatabaseStatement;
import org.javautil.dex.renderer.interfaces.RenderingException;
import org.javautil.document.MimeType;
import org.javautil.jdbc.BindVariable;
import org.javautil.jdbc.BindVariableValue;
import org.javautil.jdbc.JdbcType;
import org.javautil.jdbc.datasources.InvalidDataSourceException;
import org.javautil.logging.Events;
import org.javautil.persistence.PersistenceException;
import org.javautil.text.StringHelper;

import bsh.UtilEvalError;

public class Parser {
	/**
	 * The input reader.
	 */

	/**
	 * use document '/scratch/filename.xml'
	 * 
	 * inject as [ sibling to ] | [ child of ] | "state[@id='range']" with
	 * columns as [text | attribute]
	 */

	// Events
	private static Events events = new Events();

	private String echoUntil;

	/**
	 *
	 */
	private boolean echo = false;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	// private boolean endOfInput = false;

	private final Dexterous caller;

	private final String newline = System.getProperty("line.separator");

	private DexterousCommand commandType;

	private boolean transpose;

	private Command command;

	private final Logger traceLogger;

	private final Lexer lexer;

	/**
	 * @todo provide a method of changing
	 */
	private final boolean showInputFileAbsolutePath = false;

	/**
	 * if true doesn't return from parse evaluation until a go command is found
	 */
	private boolean parseUntilGo = false;

	private final CursorBreakScripts breakScripts = new CursorBreakScripts();

	private final DexterousState state;

	static {
		// events.addEvent(Events.NBR_LINES_TO_STDOUT);
		// events.addEvent(Events.DUMP_READ_STACK);
	}

	public Parser(final Dexterous caller, final DexterousState state) {
		// LineNumberReader inputReader, WriterSet writers) {
		// logger.setLevel(Level.DEBUG);
		if (state == null) {
			throw new IllegalArgumentException("state is null");
		}
		if (state.getInputReader() == null) {
			throw new IllegalArgumentException("inputReader is null");
		}
		if (caller == null) {
			throw new IllegalArgumentException("caller is null");
		}
		if (state.getWriters() == null) {
			throw new IllegalArgumentException("writers is null");
		}
		this.state = state;
		state.setParser(this);
		// SQLLineReader reader = new SQLLineReader(inputReader);
		lexer = new Lexer(state.getInputReader(), state.getWriters());
		lexer.setEvents(events);
		this.caller = caller;
		traceLogger = state.getTraceLogger();
	}

	public String getCurrentLine() {
		return lexer.getCurrentLine();
	}

	public Dexterous getDexterous() {
		return caller;
	}

	public Lexer getLexer() {
		return lexer;
	}

	public String getLine(final boolean tokenize) throws IOException,
			ParseException {

		lexer.readLine(tokenize);

		if (getCurrentLine() == null) {
			logger.debug("no more input");
			// tb seems like there should be a better event notification than
			// calls to parent
			// could have
			state.setEndOfInput(true); // notify caller that there is no more
										// input
		} else {
			if (echo) {
				caller.write(getCurrentLine());
			}
		}
		return getCurrentLine();
	}

	public String getLine(final int statementNumber, final boolean tokenize)
			throws IOException, ParseException {

		lexer.readLine(statementNumber, tokenize);

		if (getCurrentLine() == null) {
			state.setEndOfInput(true); // notify caller that there is no more
										// input
		} else {
			// tb - does it seem reasonable to proxy this?
			if (echo) {
				caller.write(getCurrentLine());
			}
		}
		return getCurrentLine();
	}

	public Lexer getTokenManager() {
		return lexer;
	}

	/**
	 * @return the endOfInput
	 */
	public boolean isEndOfInput() {
		return lexer.isEndOfInput();
	}

	/**
	 * @return the transpose
	 */
	public boolean isTranspose() {
		return transpose;
	}

	public void parseRequest() throws DexterousException {
		try {
			parseTheRequest();
			if (logger.isDebugEnabled()) {
				logger.debug(state.getStateInfo());
			}

			// if (state.getEvents().exists(LOG_SQL) && state.getSqlText() !=
			// null) {
			// logger.debug(state.getSqlText());
			// }
			// if (traceEvents.exists(LOG_SQL) && state.getSqlText() != null &&
			// state.getTraceLogger() != null) {
			// state.getTraceLogger().info(state.getSqlText());
			// }
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e.getStackTrace());
			throw new DexterousException(e);
		}
	}

	private void parseTheRequest() throws IOException, SQLException,
			ParseException, EndOfParseStreamException, TransformerException,
			DexterousException, RenderingException, InvalidDataSourceException,
			DexterousStateException, DocumentException, PersistenceException,
			InstantiationException, IllegalAccessException {

		String currentToken = null;
		getLine(true);

		if (getCurrentLine() == null) {
			state.setEndOfInput(true);
			return;
		}
		boolean requestComplete = false;

		while (!requestComplete && !lexer.isEndOfInput()) {
			final String[] tokens = getTokens(getCurrentLine());
			lexer.resetTokenStateToCurrentLine();

			currentToken = lexer.next();
			if (currentToken != null) {
				commandType = DexterousCommand.getCommandType(currentToken);
				logger.debug("command type " + commandType);
				System.out.print("");

				requestComplete = handleCommand(commandType, tokens);
				if (!requestComplete) {
					getLine(true);

					if (lexer.isEndOfInput()) {
						logger.info("end of input at " + getLineNumber());
						caller.popFile();
						// caller.setEndOfInput(true);
					}
				}
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	boolean handleCommand(final DexterousCommand commandType,
			final String[] tokens) throws ParseException, IOException,
			SQLException, DexterousException, InvalidDataSourceException,
			DexterousStateException, TransformerException, RenderingException,
			DocumentException, PersistenceException, InstantiationException,
			IllegalAccessException {
		// logger.info("command type " + commandType);
		boolean requestComplete = false;
		switch (commandType) {
		// case AUTOTRACE:
		// break;
		// case BEGIN_STATEMENT:
		// break;
		case BREAK:
			final Break breakCommand = new Break(getTokens(getCurrentLine(), 1));
			state.setBreaks(breakCommand.getBreaks());
			break;
		case CLEAR:
			clear();
			break;
		case COMMENT:
			break;
		case CONNECT:
			connect();
			break;
		case COMMIT:
			command = new Commit(getTokens(getCurrentLine(), 1));
			break;
		case CURSOR:
			createCursor();
			break;
		case DESCRIBE:
			if (tokens.length == 1) {
				throw new ParseException("expected object to describe");
			}
			if (tokens.length > 2) {

				throw new ParseException("one object name expected");
			}
			caller.describe(tokens[1]);
			break;
		case ECHO:
			caller.write(getCurrentLine());
			break;
		case ECHO_UNTIL:
			echoUntil(lexer.next()); // @todo this is a bug allows
			// multiple tokens and silently
			// throws one awayt
			break;
		case EXEC:
			sqlExec(getTokens(getCurrentLine(), 1));
			break;
		case EXIT:
			// endOfInput = true;
			requestComplete = true;
			state.setEndOfInput(true);
			break;
		case GENERATE:
			generate();
			break;

		// case GET:
		// if (tokens.length == 1) {
		// throw new ParseException("expected object to describe");
		// }
		// if (tokens.length > 2) {
		// throw new ParseException("one object name expected");
		// }
		//
		// caller.getStore(tokens[1]);
		// break;
		// case GO:
		// go = true;
		// break;
		case INJECT:
			final Inject injection = new Inject();
			injection.setContext(this);
			injection.parse();
			break;
		case ALTER:
		case BEGIN:
		case CREATE:
		case DECLARE:
		case DROP:
			final DatabaseStatement ddl = new DatabaseStatement(this);
			state.setSqlText(ddl.getSqlText()); // to should check in state if
												// it is not null, that is a
												// violation as it wasn't
												// processed
			state.setSqlIsSelect(false);
			break;
		case DELETE:
		case PURGE: // this is effectively ddl
		case UPDATE: // this is dml
		case TRUNCATE: // this is ddl
		case INSERT:
			lexer.resetTokenStateToCurrentLine();
			final DML dml = new DML(this);
			state.setSqlText(dml.getSqlText());
			state.setSqlIsSelect(false);
			break;
		case SELECT:
			lexer.resetTokenStateToCurrentLine();
			final DML select = new DML(this);
			state.setSqlText(select.getSqlText());
			state.setSqlIsSelect(true);
			requestComplete = true;
			break;
		case PARSE_UNTIL_GO:
			parseUntilGo = true;
			state.setParseUntilGo(parseUntilGo);
			break;
		case PASS_ALL:
			passThruUntil(">>>");
			break;
		case REM:
			break;
		case REM_SET:
			// set(getTokens(currentRow), 2);
			// break;
		case PROMPT:
			caller.write(lexer.stripFirstToken() + newline);
			break;
		case RECORD:
			// record(getTokens(currentRow), 1);
			break;
		// case RUN:
		// if (state.getSqlText() == null) {
		// logger.warn("there is no sql to run");
		// } else {
		// sqlFound = false;
		// }
		// break;
		case SHOW:
			show();
			break;

		case SET:
			set();
			break;
		case STORE:
			store();
			break;
		case SPOOL:
			spool();
			break;
		case VARIABLE:
			defineVariable();
			break;
		case RUN_FILE:
			runFile();
			break;
		case BSH:
			final BeanShellScript bs = new BeanShellScript(this);
			final String script = bs.getScriptText();
			caller.evaluate(script);
			break;
		case WRITE:
			caller.write();
			break;
		// case WHEN:
		// when();
		// break;
		}
		return requestComplete;
	}

	// /**
	// * @param endOfInput
	// * the endOfInput to set
	// */
	// public void setEndOfInput(boolean endOfInput) {
	// this.endOfInput = endOfInput;
	// }

	/**
	 * @param transpose
	 *            the transpose to set
	 */
	public void setTranspose(final boolean transpose) {
		this.transpose = transpose;
	}

	private String checkFile(final List<String> args) throws ParseException {
		final String val = checkOne(args, "A filename was expected");
		return val;
	}

	//
	//
	//
	private String checkOne(final List<String> args, final String message)
			throws ParseException {
		if (args.size() == 0) {
			throw new ParseException(message + " but no arguments were found");
		}
		if (args.size() != 1) {
			final StringBuilder b = new StringBuilder();
			for (final String arg : args) {
				b.append("'");
				b.append(arg);
				b.append("' ");
			}
			throw new ParseException(message + " got " + args.size()
					+ " arguments with values " + b.toString());

		}
		return args.get(0);

	}

	private void checkOneOf(final List<String> args,
			final String[] allowableValues, final String message)
			throws ParseException {
		final String arg = checkOne(args, message);
		boolean ok = false;
		for (final String value : allowableValues) {
			if (value.equalsIgnoreCase(arg)) {
				ok = true;
				break;
			}
		}
		StringBuilder b;
		if (!ok) {
			b = new StringBuilder();
			b.append("expected one of");
			for (final String value : allowableValues) {
				b.append(" '");
				b.append(value);
				b.append("'");
			}
			b.append("but found '");
			b.append(arg);
			b.append("'");

			throw new IllegalArgumentException(b.toString());
		}

	}

	//
	// Command handlers
	//
	private void clear() throws ParseException {
		final List<String> args = stripFirstCurrentLine();
		final String message = "'variables' expected";
		final String[] values = new String[] { "variables" };
		checkOneOf(args, values, message);
		state.clearBindVariables();
	}

	private void connect() throws IOException, ParseException,
			InvalidDataSourceException, SQLException {
		final String dataSourceName = lexer.next();
		state.setDataSourceName(dataSourceName);
	}

	private void createCursor() throws IOException, SQLException,
			ParseException, DexterousException, PersistenceException {
		int skipCount = 3;
		String recordName = null;
		final String cursorName = lexer.next();
		final String token3 = lexer.next();
		String isText;
		if (token3.equalsIgnoreCase("RECORD")) {
			recordName = lexer.next();
			skipCount = 5;
			isText = lexer.next();
		} else {
			isText = token3;
		}

		if (!isText.equalsIgnoreCase("IS")) {
			throw new ParseException("'is' expected found '" + isText
					+ "' while processing " + lexer.getCurrentLine());
		}

		// .resetTokenStateToCurrentLine();
		final DML dml = new DML(this, skipCount);
		final String mySqlText = dml.getSqlText();
		logger.debug("sql: " + newline + mySqlText);
		final String recName = recordName == null ? cursorName : recordName;
		Cursor cursor;
		try {
			cursor = state.createCursor(cursorName, mySqlText, recName);
		} catch (final UtilEvalError e) {
			throw new DexterousException(e);
		}
		if (recordName != null) {
			cursor.setRecordName(recordName);
		} else {
			cursor.setRecordName(cursorName + "_rec");
		}

	}

	private void defineVariable() throws ParseException {
		final ArrayList<String> args = stripFirstCurrentLine();
		if (args.size() != 2) {
			throw new IllegalArgumentException("variable and type are required");
		}
		final String name = args.get(0).toUpperCase();
		final String type = args.get(1).toUpperCase();
		final JdbcType jdbcType = JdbcType.getJdbcType(type);
		final BindVariable var = new BindVariable(name, jdbcType);
		state.addBindVariable(name, var);
	}

	private void echoUntil(final String endEcho) throws IOException,
			ParseException, DexterousStateException {
		final String token = lexer.nextThisLine();
		final String trimmed = token.trim();
		traceLogger.info("echoing until '" + trimmed + "'");
		passThruUntil(trimmed);

	}

	private String getEchoUntil() {
		return echoUntil;
	}

	private String[] getTokens(final String data) {
		final StringTokenizer tokenizer = new StringTokenizer(data, " \t");
		final ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	private ArrayList<String> getTokens(final String data, final int stripFirst)
			throws ParseException {
		final ArrayList<String> rc = lexer.getTokens(data);
		for (int i = 0; i < stripFirst; i++) {
			rc.remove(i);
		}

		return rc;
	}

	private void illegalArgument(final String message) throws ParseException {
		final String fileName = showInputFileAbsolutePath ? state
				.getCurrentFile().getAbsolutePath() : state.getCurrentFile()
				.getName();
		// @todo show file name
		throw new ParseException("File " + fileName + " line: "
				+ lexer.getLineNumber() + " " + message);
	}

	private void initializeEvents() {
		// events.add(EACH_LINE_READ);
	}

	//
	//
	//
	private boolean isTrue(final List<String> args) throws ParseException {
		boolean returnValue;
		final String msg = "true | false | on | off expected";
		final String val = checkOne(args, msg);

		if ("true".equalsIgnoreCase(val) || "on".equalsIgnoreCase(val)) {
			returnValue = true;
		} else if ("false".equalsIgnoreCase(val) || "off".equalsIgnoreCase(val)) {
			returnValue = false;

		} else {
			throw new IllegalArgumentException(msg + " not '" + val + "'");
		}
		return returnValue;
	}

	/**
	 * @todo This was fast and sloppy probably needs to be reviewed.
	 * @param in
	 * @return
	 * @throws ParseException
	 */

	private void passThruUntil(final String token) throws IOException,
			ParseException {
		if (token == null) {
			throw new IllegalArgumentException("token is null");
		}
		setEchoUntil(token.trim());
		String line = null;
		while ((line = getLine(false)) != null) {

			if (line.trim().equals(echoUntil)) {
				break;
			}
			caller.write(line + newline);
		}
	}

	private void runFile() throws IOException, SQLException,
			TransformerException, ParseException, DexterousException,
			RenderingException, DexterousStateException,
			InstantiationException, IllegalAccessException {
		final String fname = lexer.nextThisLine();
		final File f = new File(fname);
		caller.pushFile(f);
	}

	/**
	 * what about make
	 * 
	 * @todo should do a while true with break rather than all of these returns
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 * @throws DocumentException
	 */
	private void set() throws IOException, ParseException, DocumentException {
		final String first = lexer.nextThisLine();
		String token = null;

		while (true) {
			if (first.equalsIgnoreCase("AUTOTRACE")) {
				state.setAutoTrace(isTrue(lexer.restOfLine()));
				break;
			}
			if (first.equalsIgnoreCase("BASE")) {
				setBaseDirectory(lexer.restOfLine());
				break;
			}
			if (first.equalsIgnoreCase("DATE")) {
				setDateFormat(lexer.restOfLine());
				break;
			}
			if (first.equalsIgnoreCase("FILE")) {
				if (lexer.next().equalsIgnoreCase("DATE")) {
					if (lexer.next().equalsIgnoreCase("FORMAT")) {
						setFileDateFormat(lexer.next());
					} else {
						throw new IllegalArgumentException("FORMAT expected");
					}
				} else {
					if (lexer.next().equalsIgnoreCase("LOG")) {
						if ((token = lexer.next()).equalsIgnoreCase("LEVEL")) {
							final Level level = Level.toLevel(lexer.next());
							caller.setLogLevel(level);
						} else {
							throw new IllegalArgumentException(
									"LEVEL expected after 'set log' found "
											+ token);
						}
					}
				}

				break;
			}
			// set cursor emp_cur break on deptno
			if (first.equalsIgnoreCase("CURSOR")) {
				setCursorBreak();
				break;
				// String CURSOR = lexer.next();

			}
			if (first.equalsIgnoreCase("ECHO")) {
				echo = isTrue(lexer.restOfLine());
				lexer.setEcho(echo);
				// caller.setEcho(echo);
				break;
			}
			if (first.equalsIgnoreCase("document")) {
				setDocumentTemplate();
				break;
			}
			if (first.equalsIgnoreCase("STDOUT")) {
				if (lexer.next().equalsIgnoreCase("LOG")) {
					if ((token = lexer.next()).equalsIgnoreCase("LEVEL")) {
						Level.toLevel(lexer.next().toUpperCase());
						// caller.setConsoleHandlerLogLevel(level);
						break;
					}
					illegalArgument("LEVEL expected after 'set stdout log' found '"
							+ token + "'");
				}
			}
			if (first.equalsIgnoreCase("TERMOUT")) {
				state.setTermout(isTrue(lexer.restOfLine()));
				break;
			}
			if (first.equalsIgnoreCase("TRANSPOSE")) {
				state.setTranspose(isTrue(lexer.restOfLine()));
				break;
			}
			if (first.equalsIgnoreCase("output")) {
				setOutputFormat(lexer.restOfLine());
				break;
			}
			if (first.equalsIgnoreCase("LOG")) {
				token = lexer.next();
				if (token.equalsIgnoreCase("DIRECTORY")) {
					token = lexer.next();
					state.setLogDirectory(token);
				}
				final String fileName = checkFile(lexer.restOfLine());
				state.setDexterLogFileName(fileName);
				state.setDexterLogWriter(new FileWriter(fileName));
				break;
			}
			if (first.equalsIgnoreCase("STYLESHEET")) {
				state.setStylesheetFileName(checkFile(lexer.restOfLine()));
				break;
			}
			if (first.equalsIgnoreCase("RESULTSET")) {
				final String name = lexer.next();
				if (!"NAME".equalsIgnoreCase(name)) {
					illegalArgument("'name' expected after 'set resultset'");
				}
				final String resultSetId = lexer.next();
				state.setResultSetId(resultSetId);
				break;
			}
			if (first.equalsIgnoreCase("crosstab")) {
				final String crosstabType = lexer.next();
				if (crosstabType.equalsIgnoreCase("columns")
						|| crosstabType.equalsIgnoreCase("column")) {
					state.setCrossTabColumns(lexer.restOfLine());

				} else if (crosstabType.equalsIgnoreCase("rows")
						|| crosstabType.equalsIgnoreCase("row")) {
					state.setCrossTabRows(lexer.restOfLine());

				} else if (crosstabType.equalsIgnoreCase("values")
						|| crosstabType.equalsIgnoreCase("value")) {
					state.setCrossTabValues(lexer.restOfLine());

				} else {
					throw new IllegalArgumentException(
							"unknown crosstab type "
									+ crosstabType
									+ " should be one of 'columns' | 'rows' | 'values' ");
				}
				break;
			}
			if (first.equalsIgnoreCase("worksheet")) {
				setWorksheetName();
				break;
			}

			logger.warn("unsupported directive - if this is documented as a feature please report this bug "
					+ newline
					+ "at line"
					+ lexer.getLineNumber()
					+ " :'"
					+ lexer.getCurrentLine() + "'");
			break;
		}
	}

	private void setBaseDirectory(final List<String> args)
			throws ParseEndOfLineException {
		if (!lexer.nextThisLine().equalsIgnoreCase("DIRECTORY")) {
			throw new IllegalArgumentException("directory expected");
		}
		final String directoryToken = lexer.nextThisLine();
		final String directoryName = StringHelper
				.extractLiteral(directoryToken);
		// SimpleDateFormat sdf = new SimpleDateFormat(formatString);
		state.setBaseDirectoryName(directoryName);
	}

	private void setCursorBreak() throws ParseException, IOException {
		final String cursorName = lexer.next();
		final String BREAK = lexer.next();
		if (!BREAK.equalsIgnoreCase("BREAK")) {
			throw new ParseException("'break' expected found '" + BREAK + "'");
		}
		while (true) {
			final String ON = lexer.next();
			logger.debug("on is '" + ON + "'");

			final String columnName = lexer.next();
			lexer.next();

			if (!ON.equalsIgnoreCase("ON")) {
				throw new ParseException("'on' expected found '" + ON + "'");
			}
			final BeanShellScript bs = new BeanShellScript(this);
			final String script = bs.getScriptText();
			if (logger.isDebugEnabled()) {
				logger.debug("added script for cursor '" + cursorName
						+ "' column '" + columnName + " script " + newline
						+ script);
			}
			breakScripts.add(cursorName, columnName, script);
			if (logger.isDebugEnabled()) {
				logger.debug("at line " + lexer.getLineNumber());
			}
			if (lexer.isScriptTerminator()) {
				lexer.next(); // consume
				break;
			}

			// logger.debug("not script terminator");

		}
	}

	private void setDateFormat(final List<String> args)
			throws ParseEndOfLineException {
		if (!lexer.nextThisLine().equalsIgnoreCase("FORMAT")) {
			throw new IllegalArgumentException("format expected");
		}
		final String format = lexer.nextThisLine();
		final String formatString = StringHelper.extractLiteral(format);

		state.setDateFormat(formatString);
	}

	private void setDocumentTemplate() throws IOException, ParseException,
			DocumentException {
		final String template = lexer.next();
		if (!"template".equalsIgnoreCase(template)) {
			throw new ParseException("template expected found '" + template
					+ "'");
		}

		final String fileName = lexer.next();
		state.setDocumentTemplate(fileName);
	}

	private void setEchoUntil(final String echoUntil) {
		this.echoUntil = echoUntil;
	}

	private void setFileDateFormat(final String format) {

		try {
			new SimpleDateFormat(format);
		} catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage()
					+ "while trying to parse '" + format
					+ "' as a date formate");
		}
		if (format.contains("/")) {
			throw new IllegalArgumentException("format string '" + format
					+ "' contains illegal character '/'");
		}
		// SimpleDateFormat sdf = new SimpleDateFormat(formatString);
		state.setFileDateFormat(format);
	}

	private void setOutputFormat(final List<String> args)
			throws ParseEndOfLineException {
		if (!lexer.nextThisLine().equalsIgnoreCase("FORMAT")) {
			throw new IllegalArgumentException("format expected");
		}
		final String mimeType = lexer.nextThisLine();
		final MimeType mime = MimeType.parse(mimeType);
		state.setMimeType(mime);
	}

	private void setWorksheetName() throws ParseEndOfLineException {
		// @todo warn open office
		// @todo add check to make sure only 1 more is coming
		final String text = lexer.nextThisLine();
		logger.debug("creating worksheet '" + text + "'");
		state.setWorksheetName(text);
	}

	private void show() throws IOException, ParseException, SQLException,
			DexterousStateException {
		final String showWhat = lexer.next();
		while (true) {
			if ("WAIT".equalsIgnoreCase(showWhat)
					|| "WAITS".equalsIgnoreCase(showWhat)) {
				caller.showWait();
				break;
			}
			if ("STAT".equalsIgnoreCase(showWhat)
					|| "STATS".equalsIgnoreCase(showWhat)) {
				caller.showStats();
				break;
			}
			throw new ParseException("WAIT(S) or STAT(S) expected found "
					+ showWhat);
		}
	}

	private void spool() throws IOException, ParseEndOfLineException {
		final String fileName = lexer.nextThisLine();
		// String oldFileName = null;
		caller.spool(fileName);

	}

	private void sqlExec(final List<String> tokens) throws DexterousException {
		if (tokens.size() < 3) {
			throw new IllegalArgumentException(
					"exec variableName := value expected");
		}
		final String variableName = tokens.get(0).toUpperCase();
		final String assignOperator = tokens.get(1);
		if (!assignOperator.equals(":=")) {
			throw new IllegalArgumentException(":= expected");
		}
		final String value = tokens.get(2);
		final String cookedValue = value.replaceAll("\"", "").replaceAll("'",
				"");
		final String strippedVariableName = variableName.replaceFirst(":", "");
		final BindVariable var = state.getBindVariable(strippedVariableName);
		// BindVariable var = bindVariables.get(variableName);
		if (var == null) {
			throw new IllegalArgumentException("'" + variableName
					+ "' must be declared");
		}
		final BindVariableValue bindValue = new BindVariableValue(
				strippedVariableName, var.getJdbcType(), cookedValue);
		try {
			state.setBindVariable(bindValue.getVariableName(), bindValue);
		} catch (final UtilEvalError e) {
			throw new DexterousException(e);
		}
	}

	private void store() throws ParseException {
		final ArrayList<String> tokens = stripFirstCurrentLine();

		final String message = "usage store [distinct] column_name";
		switch (tokens.size()) {
		case 0:
			throw new IllegalArgumentException(
					"usage store [distinct] column_name");
		case 1:
			state.addStringStore(tokens.get(0));
			break;
		case 2:
			if (tokens.get(0).equalsIgnoreCase("distinct")) {
			} else {
				throw new IllegalArgumentException(message);
			}
			state.addStringStore(tokens.get(2));
			break;
		default:
			throw new IllegalArgumentException(message);

		}
	}

	private ArrayList<String> stripFirstCurrentLine() throws ParseException {
		return getTokens(getCurrentLine(), 1);
	}

	// private String getCurrentRow() {
	// return currentRow;
	// }

	// private void when() throws IOException, ParseException {
	// String colName = lexer.next();
	// String changes = lexer.next();
	// BeanShellScript bs = new BeanShellScript(this);
	// String script = bs.getScriptText();
	// caller.when(colName, script);
	// // caller.evaluate(script);
	// }

	void generate() throws SQLException, ParseException, IOException,
			InvalidDataSourceException, DexterousStateException {
		final String genType = lexer.next();
		while (true) {
			if ("SELECT".equalsIgnoreCase(genType)) {
				final String tableName = lexer.next();
				caller.generateSelect(tableName);
				break;
			}
			throw new ParseException("expected 'select");
		}
	}

	Command getCommand() {
		return command;
	}

	public int getLineNumber() {
		return lexer.getLineNumber();
	}

	void setEcho(final boolean val) {
		echo = val;
	}

	public void setStdOutPrompt(final String val) {
		lexer.setUserPrompt(val);
	}

	public DexterousCommand getCommandType() {
		return commandType;
	}
}
