package org.javautil.dex.parser;

public class ParseException extends Exception {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ParseException(final Exception e) {
		super(e);
	}

	public ParseException(final int lineNbr, final String lineText, final String message) {
		super("line # " + lineNbr + " '" + lineText + "' " + "\n" + message);

	}

	public ParseException(final String message) {
		super(message);
	}
}
