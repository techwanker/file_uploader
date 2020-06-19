package org.javautil.dex;

import java.sql.Connection;

import com.mchange.v2.c3p0.DataSources;

//import org.javautil.jdbc.DataSources;

public interface Request {
	public String getDataSourceName();

	public void process() throws ProcessingException;

	public void setConnection(Connection conn);

	public void setDataSourceName(String dataSourceName);

	public void setDataSources(DataSources ds);
}
