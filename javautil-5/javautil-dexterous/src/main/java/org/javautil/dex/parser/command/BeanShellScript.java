// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DatabaseStatement.java

package org.javautil.dex.parser.command;

import java.io.IOException;

import org.javautil.dex.parser.Lexer;
import org.javautil.dex.parser.ParseException;
import org.javautil.dex.parser.Parser;

public class BeanShellScript {
	private final Lexer lexer;

//	private Logger logger = Logger.getLogger(getClass().getName());

	boolean lookingForPlSql;

	public BeanShellScript(final Parser parser) throws ParseException {
	//	logger = Logger.getLogger(getClass().getName());
		lookingForPlSql = false;
		lexer = parser.getTokenManager();
		lexer.resetTokenStateToCurrentLine();
	}

	public BeanShellScript(final Parser parser, final boolean endsWithSlash) {
	//	logger = Logger.getLogger(getClass().getName());
		lookingForPlSql = false;
		lexer = parser.getTokenManager();
	}


	public String getScriptText() throws IOException, ParseException {

		return lexer.getShellScript();

	}


}
