package org.javautil.sqlrunner;

import java.util.ArrayList;
import java.util.List;


public class SqlStatement {
	List<String> textLines = new ArrayList<String>();
	
	public void addLine(String line) {
		textLines.add(line);
		
	}
}