package org.javautil.dex.parser;

public class LexerException extends Exception {
	public LexerException(Exception e) {
		super(e);
	}

	public LexerException(String msg) {
		super(msg);
	}
}
