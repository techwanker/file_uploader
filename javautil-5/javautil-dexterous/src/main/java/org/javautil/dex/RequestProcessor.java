package org.javautil.dex;

import java.sql.Connection;

public interface RequestProcessor {
	public void processRequest(Request r,Connection conn) throws ProcessingException;
}
