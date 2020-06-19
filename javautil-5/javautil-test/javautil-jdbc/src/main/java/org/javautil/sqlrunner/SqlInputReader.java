package org.javautil.sqlrunner;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class SqlInputReader {
	//private final static Pattern sqlSection = Pattern.compile("\\s*--\\s*sql;\\s*(.*)\\s*");
	private static final String sqlSectionRegex = "\\s*--!\\s*([Ss][Q|q][L|l];)\\s*(.*)(\\s*)";
	private final static Pattern sqlSection = Pattern.compile(sqlSectionRegex);
	private Logger logger = Logger.getLogger(this.getClass());
	
	public SqlInputReader(InputStream in) {
		
	}
	
	public boolean isSqlSection(String text) {
		Matcher matcher = sqlSection.matcher(text);
		return matcher.matches();
	}
	
	public StatementType getStatementType(String text) {
		StatementType rc = null;
		while(true) {
		if (isSqlSection(text)) {
			rc = StatementType.Section;
			break;
		}
		throw new IllegalStateException("unkown type: '" + text);
		}
		return(rc);
	}
	
	public String getSqlSectionName(String text) {
		Matcher matcher = sqlSection.matcher(text);
		boolean matches = matcher.matches();
		String msg = "'" + sqlSectionRegex + "' '" + text + "'";
		if (matches) {
			logger.info("matches: " + msg);
		} else {
			logger.info("doesn't match: " + msg);
		}
			
		String match = matcher.group(1);
		logger.info("match is '" + match + "'");
		return matcher.group(2).trim();
	}
}
