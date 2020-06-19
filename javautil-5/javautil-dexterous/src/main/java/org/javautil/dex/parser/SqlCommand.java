package org.javautil.dex.parser;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public enum SqlCommand {



	SELECT, INSERT, UPDATE, DELETE, ALTER, CREATE, DROP, TRUNCATE;

	public static final String revision = "$Revision: 1.1 $";

	private static Logger logger = Logger.getLogger(SqlCommand.class.getName());

	private static Pattern pattern = Pattern.compile("[\t ]*([^\t ]*)");

	private static boolean debug = false;

	private static HashMap<String, SqlCommand> map = new HashMap<String, SqlCommand>();
	static {
		map.put("SELECT", SELECT);
		map.put("INSERT", INSERT);
		map.put("UPDATE", UPDATE);
		map.put("DELETE", DELETE);
		map.put("ALTER", ALTER);
		map.put("CREATE", CREATE);
		map.put("DROP", DROP);
		map.put("TRUNCATE", TRUNCATE);

	}

	public static SqlCommand parse(final String text) {
		return parse(text, false);
	}

	public static SqlCommand parse(final String text, final boolean debug) {
		if (text == null) {
			throw new IllegalArgumentException("text is null");
		}

	    final String firstWordUpper = getFirstWordUpper(text);
		final SqlCommand retVal = map.get(firstWordUpper);
		if (retVal == null) {
			throw new IllegalArgumentException("not a mapped sql command: '" + firstWordUpper + "'");
		}
		return retVal;
	}

	/**
	 * parses the text and returns null if keyword not found.
	 * @param text
	 * @return
	 */

	public static SqlCommand parseText(final String text) {
		if (text == null) {
			throw new IllegalArgumentException("text is null");
		}

	    final String firstWordUpper = getFirstWordUpper(text);
		final SqlCommand retVal = map.get(firstWordUpper);

		return retVal;
	}

	private static String getFirstWordUpper(final String text) {
		final Matcher m = pattern.matcher(text);
		String firstWord = null;
		String firstUpper = null;
		if (m.find()) {
			firstWord = m.group();
			firstUpper = firstWord.toUpperCase();
		}
		if (debug) {
			logger.info("text " + text);
			logger.info("first word '" + firstWord + "'");
			logger.info("firstUpper: '" + firstUpper + "'");
		}
		return firstUpper;

	}

	@SuppressWarnings("incomplete-switch")
	public boolean isDDL() {
		boolean returnValue = false;
		switch (this) {
		case SELECT:
		case INSERT:
		case UPDATE:
		case DELETE:
			returnValue = true;

		}
		return returnValue;
	}

	@SuppressWarnings("incomplete-switch")
	public boolean isDML() {
		boolean returnValue = false;
		switch (this) {
		case ALTER:
		case CREATE:
		case DROP:
		case TRUNCATE:
			returnValue = true;
		}
		return returnValue;
	}
}
