package org.javautil.dex.parser.command;

import java.util.List;

import org.javautil.dex.dexterous.DexterousCommand;

public class DateFormat implements Command {
	private String dateFormat;

	public DateFormat(final List<String> tokens) {

		if (tokens.size() != 1) {
			throw new IllegalArgumentException("set date format " + " requires an argument");
		}
		final String arg =tokens.get(1);
		dateFormat = arg.replaceAll("^'", "").replaceAll("^\"", "");
		dateFormat = dateFormat.replaceAll("'$", "").replaceAll("\"$", "");

	}

	public DexterousCommand getDexterousCommand() {
		return DexterousCommand.BREAK;
	}


	public String getValue() {
		return dateFormat;
	}
}
