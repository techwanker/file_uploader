package org.javautil.dex.parser.command;

import java.util.ArrayList;
import java.util.List;

import org.javautil.dex.dexterous.DexterousCommand;

public class Break implements Command {
	ArrayList<String> breaks = new ArrayList<String>();
	public Break(final List<String> tokens) {

		if (tokens.size() == 0) {
			throw new IllegalArgumentException("break requires an argument");
		}


		if ("off".equalsIgnoreCase(tokens.get(0))) {
			if (tokens.size() == 1) {
				breaks = null;
			} else {
				throw new IllegalArgumentException("unexpected input after break off");
			}
		} else {
			for (final String token : tokens) {
				if (!"ON".equalsIgnoreCase(token)) {
					breaks.add(token);
				}
			}

		}

	}

	public ArrayList<String> getBreaks() {
		return breaks;
	}


	public DexterousCommand getDexterousCommand() {
		return DexterousCommand.BREAK;
	}
}
