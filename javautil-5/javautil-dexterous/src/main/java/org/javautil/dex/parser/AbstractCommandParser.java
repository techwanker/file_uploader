package org.javautil.dex.parser;

import java.io.IOException;
import java.util.ArrayList;

import org.javautil.dex.dexterous.Dexterous;

public abstract class AbstractCommandParser {
	private Parser parser;
	private Lexer lexer;
	private final ArrayList<String> tokens = new ArrayList<String>();


	public static void ensure(final String text, final String expected) throws ParseException {
		if (!expected.equalsIgnoreCase(text)) {
			throw new ParseException("expected '" + expected + "' not '" + text + "'");
		}
	}

	public static  void ensure(final String text, final String[] expected) throws ParseException {
		if (expected == null) {
			throw new IllegalArgumentException("expected is null");
		}
		if (text == null) {
			throw new IllegalArgumentException("texzt is null");
		}
		boolean found = false;
		for (int i = 0; i < expected.length; i++) {
			if (expected[i].equalsIgnoreCase(text)) {
				found = true;
				break;
			}
		}
		if (!found) {
			final StringBuilder b = new StringBuilder();
			b.append("expected on of " );
			for (int i = 0; i < expected.length; i++) {
				b.append("'");
				b.append(expected[i]);
				b.append("' ");
			}
			b.append("found " + text);
			throw new ParseException(b.toString());
		}
	}
	public Dexterous getDexterous() {
		return parser.getDexterous();
	}

	public Lexer getLexer() {
		return parser.getLexer();
	}
	public abstract void parse() throws IOException, ParseException;


	public void setContext(final Parser parser) {
		if (parser == null) {
			throw new IllegalArgumentException("parser is null");
		}
		this.parser = parser;
		this.lexer = parser.getLexer();
		if (lexer == null) {
			throw new IllegalStateException("parser returns null lexer");
		}
	}

	public void validateState() {
		if (parser == null) {
			throw new IllegalStateException("setContext has not been called");
		}
	}

	String getToken() throws IOException, ParseException {
		final String token = lexer.next();
		tokens.add(token);
		return token;
	}
	/**
	 * TODO strips " from strings in getToken
	 * could either wrap tokens with type add a parallel token type or reposition up this point in the string to get
	 * the input with all double spaces etc but this would require popping back lines on the input could
	 * @return
	 */
	String getTokensAsString() {
		final StringBuilder b = new StringBuilder();
		for (final String t : tokens) {
			b.append(t);
			b.append(" ");
		}
		return b.toString();
	}
}
