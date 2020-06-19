package org.javautil.sql;

import java.beans.PropertyVetoException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import org.javautil.misc.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

//import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * . This is a JavaBean-style class with a public, no-arg constructor, but
 * before you use the DataSource, you'll have to be sure to set at least the
 * property
 */

public class DataSourceFactory {
	private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

	private Map<String, Map<String, Object>> dataSources;

	private String yamlFileName = null;

	public DataSourceFactory() {
		String homeDir = System.getProperty("user.home");
		String yamlName = homeDir + "/connections_java.yaml";
		this.yamlFileName = yamlName;
		try {
			loadDataSources(yamlName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public DataSourceFactory(String yamlName) throws FileNotFoundException {
		this.yamlFileName = yamlName;
		loadDataSources(yamlName);
	}

	public DataSourceFactory(InputStream is) {
		Yaml yaml = new Yaml();
		dataSources = (Map<String, Map<String, Object>>) yaml.load(is);
	}

	void loadDataSources(String yamlName) throws FileNotFoundException {
		Yaml yaml = new Yaml();
		InputStream ios = new FileInputStream(new File(yamlName));
		dataSources = (Map<String, Map<String, Object>>) yaml.load(ios);
	}

	public DataSource getDatasource(String dataSourceName) throws PropertyVetoException {
		Map<String, Object> parms = dataSources.get(dataSourceName);
		if (parms == null) {
			throw new IllegalArgumentException(
					"no such dataSourceName: " + dataSourceName + " in " + this.yamlFileName);
		}
		return getDatasource(parms);
	}

	static String checkStringParm(String parmName, Map<String, Object> parms) {
		String retval = (String) parms.get(parmName);
		if (retval == null) {
			logger.warn("required parm " + parmName + " is null in " + parms);
		}
		return retval;
	}

	public DataSourceHelper getDataSourceHelper(DataSource datasource) throws PropertyVetoException {
		Map<String, Object> parms = new HashMap<String, Object>();
		DataSourceHelper dsh = new DataSourceHelper(datasource, null, parms);
		return dsh;
	}

	public DataSourceHelper getDataSourceHelper(String dataSourceName) throws PropertyVetoException {
		Map<String, Object> parms = dataSources.get(dataSourceName);
		DataSource ds = getDatasource(parms);
		DataSourceHelper dsh = new DataSourceHelper(ds, null, parms);
		return dsh;
	}

	public static DataSource getDatasource(Map<String, Object> parms) throws PropertyVetoException {
		Timer timer = new Timer();
		if (parms == null) {
			throw new IllegalArgumentException("parms is null");
		}
		HikariConfig config = new HikariConfig();
		HikariDataSource ds;
		String driver_class = checkStringParm("driver_class", parms);
		String url = checkStringParm("url", parms);
		String user = checkStringParm("username", parms);
		String password = (String) parms.get("password");
		config.setJdbcUrl(url);
		config.setUsername(user);
		config.setPassword(password);
		if (url.startsWith("jdbc:postgresql:") && config.getConnectionInitSql() == null) {
			config.setConnectionTestQuery("select 'x'");
		}
		ds = new HikariDataSource(config);
		logger.info("millis: " + timer.getElapsedMillis() + " driver_class: " + driver_class + " url: " + url
				+ " user: " + user + " password:" + password);

		return ds;

	}
	
	public static DataSource getDataSource(String url, String username, String password) {
		final HikariConfig config = new HikariConfig();
		config.setJdbcUrl(url);
		config.setUsername(username);
		config.setPassword(password);
		config.setAutoCommit(false);

		return new HikariDataSource(config);
	}

	public static DataSource getInMemoryDataSource() {

		return getDataSource("jdbc:h2:mem:test", "sa", "tutorials");
	}

	public static DataSource getInMemoryDataSourceSingleton() {
		return getDataSource("jdbc:h2:mem", "sa", "tutorials");
	}

	public static DataSource getDataSourceSpringProperties(Properties properties) {
		return ApplicationPropertiesDataSource.getDataSource(properties);
	}

	public static DataSource getDataSourceFromEnvironment() {
		return EnvironmentDataSource.getDataSource();
	}
	
	public static DataSource getH2Permanent(String path, String username, String password) {
		return getDataSource("jdbc:h2:" + path, username, password);
	}

	
}
