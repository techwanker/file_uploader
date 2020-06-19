package org.javautil.dex.dexterous;

import java.io.File;
import java.io.LineNumberReader;

import org.javautil.dex.parser.Parser;

public class FileState {
	private final File requestFile;
	private final LineNumberReader reader;
	private final Parser parser;

	public FileState(final File requestFile,final LineNumberReader reader, final Parser parser) {
		this.requestFile= requestFile;
		this.reader =reader;
		this.parser = parser;
	}

	public Parser getParser() {
		return parser;
	}

	public LineNumberReader getReader() {
		return reader;
	}

	public File getRequestFile() {
		return requestFile;
	}

}
