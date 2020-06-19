package org.javautil.dex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.javautil.io.WriterSet;

public interface Dexterity {
	public void setWriters(WriterSet writers);
	public void spool(File f) throws FileNotFoundException, IOException;
}
