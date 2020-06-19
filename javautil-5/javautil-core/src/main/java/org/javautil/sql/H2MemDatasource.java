package org.javautil.sql;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;


import javax.sql.DataSource;



public class H2MemDatasource{
	public static DataSource getDataSource()  throws PropertyVetoException, SQLException { 
	HashMap<String,Object> parms = new HashMap<String,Object>();
	parms.put("driver_class","org.h2.Driver");
	parms.put("url","jdbc:h2:mem:test");
	DataSource ds = DataSourceFactory.getDatasource(parms);
    return ds;
}
}