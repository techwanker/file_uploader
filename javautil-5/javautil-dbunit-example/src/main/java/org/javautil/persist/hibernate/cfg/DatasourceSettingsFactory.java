package org.javautil.persist.hibernate.cfg;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.SettingsFactory;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.DatasourceConnectionProvider;

/**
 * Used with a hibernate configuration to create a session factory from a
 * java.sql.DataSource. Generally only used with a
 * DatasourceFactoryConfiguration.
 * 
 * @author bcm
 */
public class DatasourceSettingsFactory extends SettingsFactory {

	private static final long serialVersionUID = -2821238988750196383L;

	private DataSource datasource;

	public DatasourceSettingsFactory() {
	}

	public DatasourceSettingsFactory(DataSource datasource) {
		setDatasource(datasource);
	}

	@Override
	protected ConnectionProvider createConnectionProvider(Properties properties) {
		DatasourceConnectionProvider provider = new DatasourceConnectionProvider();
		if (datasource == null) {
			throw new IllegalStateException("datasource is null");
		}
		provider.setDataSource(datasource);
		return provider;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

}
