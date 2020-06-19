package org.javautil.dblogging.installer;

import java.io.Closeable;
import java.sql.Connection;

import javax.sql.DataSource;

import org.javautil.dblogging.DbloggerPropertiesDataSource;
import org.javautil.sql.SqlSplitterException;
import org.junit.Test;

public class DbloggerOracleInstallTest {
    
    @Test
    public void testDrop() throws SqlSplitterException, Exception {
        DataSource loggerDatasource = new DbloggerPropertiesDataSource("dblogger.oracle.properties").getDataSource();
        Connection loggerConnection = loggerDatasource.getConnection();
         
        DbloggerOracleInstall oralog = new DbloggerOracleInstall(loggerConnection,true,false);
        oralog.drop();
        oralog.process();
        loggerConnection.close();
        ((Closeable)loggerDatasource).close();   
    }
}