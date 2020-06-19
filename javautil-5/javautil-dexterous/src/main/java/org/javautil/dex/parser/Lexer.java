package org.javautil.dex.parser;

import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.javautil.dex.dexterous.Dexterous;
import org.javautil.io.WriterSet;
import org.javautil.logging.Events;

// Referenced classes of package org.javautil.parse:
//            LexerException
// TODO should probably use <% and %> as script terminators and wrap tokens
// TODO lexer should switch state based on whether processing scripts, directives or SQL
//* TODO strips " from strings in getToken
//* could either wrap tokens with type add a parallel token type or reposition up this point in the string to get
//* the input with all double spaces etc but this would require popping back lines on the input could
//* could also have a reset state that knows what can be thrown away , this would be called from the parser
public class Lexer {
	private final LineNumberReader reader;

	private ArrayList<String> tokenList;

	private int index;

	private boolean lookingForPlSql;

	private boolean moreSQL;

	private final Logger logger = Logger.getLogger(getClass().getName());

	private StringBuilder sql;

	private final String newline;

	private boolean endOfInput;

	private final WriterSet writers;

	// private boolean moreInput;

	private String currentLine;

	private String userPrompt;

	private Events events = new Events();

	private boolean isScriptEnd = false;

	private boolean isScriptTerminator = false;

	public Lexer(final LineNumberReader reader, final WriterSet writers) {
		tokenList = new ArrayList<String>();
		index = -1;

		newline = System.getProperty("line.separator");
		// moreInput = true;
		currentLine = null;
		// events = new EventHelper();
		if (reader == null) {
			throw new IllegalArgumentException("reader is null");
		}
		if (writers == null) {
			throw new IllegalArgumentException("writers is null");
		}
		this.writers = writers;
		this.reader = reader;
		return;
	}

	public static String stripTokens(final String in, final int stripCount) {
		boolean inQuote = false;
		// StringBuilder line = new StringBuilder();
		if (stripCount < 0) {
			throw new IllegalArgumentException("stripCount < 0");
		}
		final ArrayList<String> rc = new ArrayList<String>();
		int stillToBeStrippedCount = stripCount;
		for (final StringTokenizer sst = new StringTokenizer(in, "\"\t' ", true); sst
				.hasMoreTokens();) {
			if (stillToBeStrippedCount > 0) {
				final String t = sst.nextToken();
				if (!(t.equals(" ") || t.equals("\t"))) {
					stillToBeStrippedCount--;

					if (t.equals("'") || t.equals("\"")) {
						final StringBuilder quoted = new StringBuilder();
						String u;
						for (; sst.hasMoreTokens(); quoted.append(u)) {
							inQuote = true;
							u = sst.nextToken();
							if (!u.equals(t)) {
								continue;
							}
							inQuote = false;
							break;
						}

						if (inQuote) {
							throw new IllegalArgumentException(
									(new StringBuilder(
											"improperly terminated quoted string "))
											.append(in).toString());
						}
						rc.add(quoted.toString());
					}
				} else {
					break;
				}
			}
			// @todo would be nice to be able to get a literal representation of
			// input preserving white space

		}

		if (rc.size() > 0) {
			final String lastToken = rc.get(rc.size() - 1);
			if (lastToken.endsWith(";")) {
				final String stripped = lastToken.substring(0,
						lastToken.length() - 1);
				rc.set(rc.size() - 1, stripped);
			}
		}
		final StringBuilder sb = new StringBuilder();
		for (final String s : rc) {
			sb.append(s);
			sb.append(" ");
		}
		return sb.toString();
	}

	public String getCurrentLine() {
		return currentLine;
	}

	public Events getEvents() {
		return events;
	}

	public int getLineNumber() {
		return reader.getLineNumber();
	}

	public String getPlSql() throws IOException, ParseException,
			EndOfParseStreamException {
		lookingForPlSql = true;
		final String returnValue = getStatement();
		return returnValue;
	}

	/**
	 * Get the shell script.
	 * 
	 * Expects that the preceding '[' character was consumed by a call to
	 * next(). Consumes all text until a single line is found with a "]'
	 * character as the only non-white token.
	 * 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws EndOfParseStreamException
	 */
	public String getShellScript() throws IOException, ParseException,
			EndOfParseStreamException {
		final StringBuilder script = new StringBuilder();
		// moreSQL = true;
		// boolean processedCurrentLine = false;
		while (true) {
			final String scriptLine = readLine(false);

			if (isScriptEnd || isScriptTerminator) {
				break;
			}
			script.append(scriptLine);
			script.append(newline);
		}
		final String scriptText = script.toString();
		return scriptText;
	}

	public String getStatement() throws IOException, ParseException,
			EndOfParseStreamException {
		sql = new StringBuilder();
		moreSQL = true;
		boolean processedCurrentLine = false;
		while (moreSQL) {
			String lastLine = null;
			if (!processedCurrentLine) {
				lastLine = getCurrentLine();
				processedCurrentLine = true;
			} else {
				lastLine = readLine(false);
			}
			boolean lastLineIsVirguleEOS = isVirguleEOS(lastLine);
			if (lastLineIsVirguleEOS) {
				moreSQL = false;
			} else if (lookingForPlSql) {
				if (!lastLineIsVirguleEOS) {
					sqlAppend(lastLine);
				}
			} else if (isSemiColonEOS(lastLine)) {
				moreSQL = false;
				sqlAppend(stripTrailingSemicolon(lastLine));
			} else {
				sqlAppend(lastLine);
			}
		}
		final String sqlText = sql.toString();
		logger.info((new StringBuilder("returning sql:\n")).append(sqlText)
				.toString());
		return sqlText;
	}

	public ArrayList<String> getTokens(final String in) throws ParseException {
		boolean inQuote = false;
		final ArrayList<String> rc = new ArrayList<String>();
		for (final StringTokenizer sst = new StringTokenizer(in, "\"\t' ", true); sst
				.hasMoreTokens();) {
			final String t = sst.nextToken();
			if (!t.equals(" ") && !t.equals("\t")) {
				if (!t.equals("'") && !t.equals("\"")) {
					rc.add(t);
				} else {
					final StringBuilder quoted = new StringBuilder();
					String u;
					for (; sst.hasMoreTokens(); quoted.append(u)) {
						inQuote = true;
						u = sst.nextToken();
						if (!u.equals(t)) {
							continue;
						}
						inQuote = false;
						break;
					}

					if (inQuote) {
						throw new ParseException((new StringBuilder(
								"improperly terminated quoted string "))
								.append(in).toString());
					}
					rc.add(quoted.toString());
				}
			}
		}

		isScriptTerminator = false;
		if (rc.size() == 1) {
			if ("];".equals(rc.get(0))) {
				isScriptTerminator = true;

			}
		}
		if (rc.size() > 0) {
			final String lastToken = rc.get(rc.size() - 1);

			if (lastToken.endsWith(";")) {
				final String stripped = lastToken.substring(0,
						lastToken.length() - 1);
				rc.set(rc.size() - 1, stripped);
			}
		}
		return rc;
	}

	public String getUserPrompt() {
		return userPrompt;
	}

	public boolean isEndOfInput() {
		return endOfInput;
	}

	// public boolean isLastLine() {
	// return !isMoreInput();
	// }

	public boolean isLookingForPlSql() {
		return lookingForPlSql;
	}

	public boolean isScriptTerminator() {
		return isScriptTerminator;
	}

	public String next() throws IOException, ParseException {
		index++;
		String returnValue = null;
		@SuppressWarnings("unused")
		int tokensInLine; // facilitates debugging watch
		while ((tokensInLine = tokenList.size()) <= index && !endOfInput) {
			readLine(true);
		}

		if (!endOfInput) {
			returnValue = tokenList.get(index);
		}
		return returnValue;
	}

	public String nextThisLine() throws ParseEndOfLineException {
		index++;
		String returnValue = null;
		if (tokenList.size() > index) {
			returnValue = tokenList.get(index);
		} else {
			throw new ParseEndOfLineException("no more tokens this line");
		}
		if (!returnValue.startsWith("'")) {
			returnValue.startsWith("\"");
		}
		return returnValue;
	}

	public String nextThisLine(final int maxTokensLeft)
			throws ParseEndOfLineException {
		index++;
		final int tokensLeft = tokenList.size() - index;
		if (tokensLeft > maxTokensLeft) {
			throw new IllegalStateException((new StringBuilder(
					"expected no more than ")).append(maxTokensLeft)
					.append(" before returning this token but have ")
					.append(tokensLeft).toString());
		}
		String returnValue = null;
		if (tokenList.size() > index) {
			returnValue = tokenList.get(index);
		} else {
			throw new ParseEndOfLineException("no more tokens this line");
		}
		return returnValue;
	}

	public String peek(final int offset) throws LexerException {
		final int retval = index - offset;
		if (index - offset < 0) {
			throw new LexerException((new StringBuilder("invalid index "))
					.append(retval).toString());
		}
		tokenList.size();
		return tokenList.get(retval);

	}

	public void pushBack() {
		if (index < 1) {
			throw new IllegalStateException("at first token");
		}
		index--;
		return;

	}

	public String readLine(final boolean tokenize) throws IOException,
			ParseException {
		if (getUserPrompt() != null) {
			writers.write(getUserPrompt(), Dexterous.WRITE_PROMPTS);
		}
		currentLine = reader.readLine();
		if (currentLine == null) {
			setEndOfInput();
		}
		index = 0;
		boolean dumpStack = events
				.isRegistered(DexterousEvents.DUMP_READ_STACK);
		if (dumpStack) {
			Thread.dumpStack();
			logger.info((new StringBuilder("# "))
					.append(reader.getLineNumber()).append(" '")
					.append(currentLine).append("'").toString());
		}
		if (getCurrentLine() != null && tokenize) {
			tokenizeCurrentLine();
		}
		// final boolean traceInput = events.exists(Events.NBR_LINES_TO_STDOUT);
		// if (traceInput && !dumpStack) {
		// logger.debug((new StringBuilder(String.valueOf(reader
		// .getLineNumber()))).append("# ").append(currentLine)
		// .toString());
		// }
		final boolean echoToStdout = events
				.isRegistered(DexterousEvents.ECHO_LINES_TO_STDOUT);

		if (echoToStdout) {

			if (currentLine != null) {
				final StringBuilder sb = new StringBuilder();
				sb.append(String.valueOf(reader.getLineNumber()));
				sb.append("# ");
				sb.append(currentLine);
				final String msg = sb.toString();
				System.out.println(msg);
			}

		}
		return getCurrentLine();
	}

	public String readLine(final int lineNumber, final boolean tokenize)
			throws IOException, ParseException {
		logger.debug("before write > prompt");
		// writers.write(lineNumberText, Dexterous.WRITE_PROMPTS);

		currentLine = reader.readLine();
		index = 0;
		// boolean dumpStack = events.exists(Events.DUMP_READ_STACK);
		// if (dumpStack) {
		// Thread.dumpStack();
		// logger.info((new StringBuilder("# "))
		// .append(reader.getLineNumber()).append(" '")
		// .append(currentLine).append("'").toString());
		// }
		if (getCurrentLine() != null && tokenize) {
			tokenizeCurrentLine();
		}
		return getCurrentLine();
	}

	public void resetTokenStateToCurrentLine() throws ParseException {
		tokenList = new ArrayList<String>();
		index = -1;
		tokenizeCurrentLine();
	}

	public List<String> restOfLine() {
		final int firstIndex = index + 1;
		final int lastIndex = tokenList.size() - 1;
		final ArrayList<String> retval = new ArrayList<String>();
		for (int i = firstIndex; i <= lastIndex; i++) {
			final String s = tokenList.get(i);
			if (events.isRegistered(DexterousEvents.REST_OF_LINE)) {
				logger.debug((new StringBuilder("adding ")).append(s)
						.toString());
			}
			retval.add(s);
		}
		logger.debug("exiting restOfLine");
		return retval;
	}

	public void setEcho(final boolean val) {
		if (val) {
			logger.debug("adding ECHO_LINES_TO_STDOUT_EVENT");
			events.registerEventName(DexterousEvents.ECHO_LINES_TO_STDOUT);
		} else {
			events.deregisterEventName(DexterousEvents.ECHO_LINES_TO_STDOUT);
		}
	}

	public void setEvents(final Events events) {
		this.events = events;
	}

	public void setLookingForPlSql(final boolean endsWithVirgule) {
		lookingForPlSql = endsWithVirgule;
	}

	public void setUserPrompt(final String userPrompt) {
		this.userPrompt = userPrompt;
	}

	public String stripFirstToken() {
		String retval = null;
		final String c = currentLine;
		int index;
		for (index = 0; index < c.length(); index++) {
			final char ch = c.charAt(index);
			if (ch != ' ' && ch != '\t') {
				break;
			}
		}

		for (; index < c.length(); index++) {
			final char ch = c.charAt(index);
			if (ch == ' ' || ch == '\t') {
				break;
			}
		}

		if (++index < c.length()) {
			retval = c.substring(index);
		} else {
			retval = "";
		}
		return retval;
	}

	// private boolean isMoreInput() {
	// return moreInput;
	// }

	private boolean isSemiColonEOS(final String text) {
		boolean retval = false;
		if (text == null) {
			throw new IllegalArgumentException("text is null");
		}
		if (text.length() == 0) {
			retval = true;
		} else {
			for (int i = text.length() - 1; i >= 0; i--) {
				final char c = text.charAt(i);
				if (isWhite(c) || c != ';') {
					continue;
				}
				retval = true;
				break;
			}
		}
		return retval;
	}

	private boolean isVirguleEOS(final String text) {
		boolean retval = false;
		int virguleCount = 0;
		boolean foundNonEOS = false;
		if (text == null) {
			throw new IllegalArgumentException("text is null");
		}
		for (int i = 0; i < text.length(); i++) {
			final char t = text.charAt(i);
			if (t == '/') {
				virguleCount++;
				continue;
			}
			if (isWhite(t)) {
				continue;
			}
			foundNonEOS = true;
			break;
		}

		if (virguleCount == 1 && !foundNonEOS) {
			retval = true;
		}
		return retval;
	}

	private boolean isWhite(final char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}

	// private void setCurrentLine(String currentLine) {
	// this.currentLine = currentLine;
	// }
	//
	// private void setMoreInput(boolean moreInput) {
	// this.moreInput = moreInput;
	// }

	private void sqlAppend(final String text) {
		sql.append(text);
		sql.append(newline);
	}

	private String stripTrailingSemicolon(final String text)
			throws ParseException {
		int length = -1;
		for (int i = text.length() - 1; i < text.length() && i > 0; i--) {
			final char t = text.charAt(i);
			if (t == ';') {
				length = i;
				break;
			}
			if (!isWhite(t)) {
				throw new IllegalStateException(
						"I am stupidly not allowing ';' in any part of any statement have to check for literals");
			}
		}

		if (length == -1) {
			throw new ParseException(getLineNumber(), getCurrentLine(),
					(new StringBuilder("semicolon not found in '"))
							.append(text).append("'").toString());
		}
		return text.substring(0, length);
	}

	// sure not proud of this token enumeration but...
	private void tokenizeCurrentLine() throws ParseException {
		final String currentLine = getCurrentLine();
		tokenList = getTokens(currentLine);

		if (tokenList.size() == 1) {
			final String token = tokenList.get(0);
			isScriptEnd = token.equals("]") || token.equals("%>");
			// isScriptTerminator = token.equals("];");
		} else {
			isScriptEnd = false;
			// isScriptTerminator = false;
		}
		// index = 0;
	}

	private void setEndOfInput() {
		this.endOfInput = true;
	}
}
