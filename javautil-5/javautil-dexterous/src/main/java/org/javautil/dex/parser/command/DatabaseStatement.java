// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(3)
// Source File Name:   DatabaseStatement.java

package org.javautil.dex.parser.command;

import java.io.IOException;

import org.apache.log4j.Logger;

import org.javautil.dex.parser.Lexer;
import org.javautil.dex.parser.ParseException;
import org.javautil.dex.parser.Parser;


public class DatabaseStatement {
	private final Lexer tm;

	private final Logger logger;

	boolean lookingForPlSql;

	public DatabaseStatement(final Parser parser) throws ParseException {
		logger = Logger.getLogger(getClass().getName());
		lookingForPlSql = false;
		tm = parser.getTokenManager();
		tm.resetTokenStateToCurrentLine();
	}

	public DatabaseStatement(final Parser parser, final boolean endsWithSlash) {
		logger = Logger.getLogger(getClass().getName());
		lookingForPlSql = false;
		tm = parser.getTokenManager();
	}

	public String getSqlText() throws IOException, ParseException {
		tm.resetTokenStateToCurrentLine();
		final String one = tm.next();
		setLookingForPlSql(false);
		if (one.equalsIgnoreCase("CREATE")) {
			final String two = tm.next();
			if (isPlSqlObject(two)) {
				setLookingForPlSql(true);
			}
			if (two.equalsIgnoreCase("OR")) {
				final String three = tm.next();
				if (three.equalsIgnoreCase("REPLACE")) {
					final String four = tm.next();
					if (isPlSqlObject(four)) {
						setLookingForPlSql(true);
					}
				}
			}
		}
		final String sqlText = tm.getStatement();
		logger.info((new StringBuilder("sqlText:\n ")).append(sqlText)
				.toString());
		return sqlText;
	}

	public boolean isLookingForPlSql() {
		return lookingForPlSql;
	}

	public boolean isPlSqlObject(final String token) {
		return token.equalsIgnoreCase("PROCEDURE")
				|| token.equalsIgnoreCase("TYPE")
				|| token.equalsIgnoreCase("FUNCTION")
				|| token.equalsIgnoreCase("PACKAGE");
	}

	private void setLookingForPlSql(final boolean val) {
		lookingForPlSql = val;
		tm.setLookingForPlSql(val);
	}

}
