package org.javautil.dex.parser;

public class EndOfParseStreamException extends ParseException {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public EndOfParseStreamException(final Exception e) {
		super(e);
	}

	public EndOfParseStreamException(final String message) {
		super(message);
	}
}
