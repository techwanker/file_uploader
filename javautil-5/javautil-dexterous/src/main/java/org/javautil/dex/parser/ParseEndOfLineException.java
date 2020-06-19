package org.javautil.dex.parser;

public class ParseEndOfLineException extends ParseException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public ParseEndOfLineException(final Exception e) {
		super(e);
	}

	public ParseEndOfLineException(final int lineNbr, final String lineText, final String message) {
		super("line # " + lineNbr + " '" + lineText + "' " + "\n" + message);

	}

	public ParseEndOfLineException(final String message) {
		super(message);
	}
}
