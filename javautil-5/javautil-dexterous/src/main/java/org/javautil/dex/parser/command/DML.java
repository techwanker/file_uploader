package org.javautil.dex.parser.command;

import java.io.IOException;

import org.apache.log4j.Logger;

import org.javautil.dex.dexterous.DexterousCommand;
import org.javautil.dex.parser.Lexer;
import org.javautil.dex.parser.ParseException;
import org.javautil.dex.parser.Parser;

public  class DML implements Command {
	private String currentRow;

	private final Parser	parser;

	private String strippedText = null;

	private final Logger  logger = Logger.getLogger(this.getClass().getName());
	public DML(final Parser parser) {
		this.parser = parser;
		currentRow = parser.getCurrentLine();
	}

	// @todo this is ugly collapse with
	public DML(final Parser parser, final int stripCount)  {
		if (parser == null) {
			throw new IllegalArgumentException("parser is null");
		}

		this.parser = parser;
		currentRow = Lexer.stripTokens(parser.getCurrentLine(),stripCount);

	}
	public DexterousCommand getDexterousCommand() {
		return DexterousCommand.DML;
	}


	public String getSqlText() throws IOException, ParseException  {
		final StringBuilder builder = new StringBuilder();

		if (currentRow == null) {
			currentRow = parser.getCurrentLine();
		}
		int lineNumber = 1;

		while (currentRow != null) {
			String row = currentRow;
			if (currentRow.trim().endsWith(";")) {

				final int semiColonAt = currentRow.lastIndexOf(";");
				row = row.substring(0, semiColonAt);
				strippedText = currentRow.substring(semiColonAt);
				builder.append(row);
			//	builder.append("\n");
				break;
			}
			builder.append(row);
			builder.append("\n");
			lineNumber++;
			currentRow = parser.getLine(lineNumber,false);
		}
		logger.debug("sql is \n" + builder.toString());
		return builder.toString();
	}

	/**
	 * The text that was stripped off the end of the statement to make it valid SQL, e.g. the trailing ';' and any following
	 * white space
	 * @return
	 */
	public String getStrippedText() {
		return strippedText;
	}
}
