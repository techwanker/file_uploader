package org.javautil.dex.parser.command;

import java.util.List;

import org.javautil.dex.dexterous.DexterousCommand;

public class Commit implements Command {
	public  Commit(final List<String> args) {
		if (args.size() > 0 ) {
			throw new IllegalArgumentException("no arguments expected");
		}
	}

	public DexterousCommand getDexterousCommand() {

		return DexterousCommand.COMMIT;
	}


}
