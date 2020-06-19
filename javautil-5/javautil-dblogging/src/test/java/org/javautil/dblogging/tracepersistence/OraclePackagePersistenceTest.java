package org.javautil.dblogging.tracepersistence;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.javautil.dblogging.DbloggerPropertiesDataSource;
import org.javautil.dblogging.installer.DbloggerOracleInstall;
import org.javautil.dblogging.logger.Dblogger;
import org.javautil.dblogging.logger.SplitLoggerForOracle;
import org.javautil.sql.ApplicationPropertiesDataSource;
import org.javautil.sql.Binds;
import org.javautil.sql.SqlSplitterException;
import org.javautil.sql.SqlStatement;
import org.javautil.util.NameValue;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OraclePackagePersistenceTest {

    private static DataSource applicationDataSource;
    private static Connection applicationConnection;
    private static DataSource loggerDataSource;
    private static Connection loggerConnection;
    private static Dblogger dblogger;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private boolean showSql = false;
    
    @BeforeClass
    public static void beforeClass() throws SqlSplitterException, Exception {
        String propertyPath = "src/test/resources/logger_and_application.properties";
        FileInputStream fis = new FileInputStream(propertyPath);
        Properties properties = new Properties();
        properties.load(fis);
   
        applicationDataSource = ApplicationPropertiesDataSource.getDataSource(properties);
        applicationConnection = applicationDataSource.getConnection();
//        loggerDataSource = DbloggerPropertiesDataSource.getDataSource(properties);
//        loggerConnection = loggerDataSource.getConnection();
//        dblogger = new SplitLoggerForOracle(applicationConnection, loggerConnection);
//        
        new DbloggerOracleInstall(applicationConnection,true,false).process();
//        new DbloggerOracleInstall(loggerConnection,true,false).process();
         
    }
    
    @AfterClass
    public static void afterClass() throws IOException {
    //   ((Closeable) applicationDataSource).close();
       ((Closeable) loggerDataSource).close();
    }
    
    @Test
    public void test() throws SQLException {
        OraclePackagePersistence opp = new OraclePackagePersistence(applicationConnection);
        long jobLogId = opp.getNextJobLogId();
        opp.persistJob("A", getClass().getName(), "moduleName", "statusMsg", "/tmp/x", "SR", jobLogId);
        opp.insertStep("stepName", "stepInfo", getClass().getName(), "stack");
        Connection testConnection = applicationDataSource.getConnection();
        //
        SqlStatement jobStmt  = new SqlStatement("select * from job_log where job_log_id = :job_log_id",testConnection);
        Binds b = new Binds();
        b.put("job_log_id", jobLogId);
        NameValue jobNv = jobStmt.getNameValue(b,true);
        logger.debug("jobNv: {}", jobNv.toString());
        //
        SqlStatement stepStmt = new SqlStatement("select * from job_step where job_log_id = :job_log_id");
        NameValue stepNv = stepStmt.getNameValue(b,true);
        logger.debug("stepNv: {}", stepNv.toString());
     
    }
}
