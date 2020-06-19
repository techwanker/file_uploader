package org.javautil.jdbc.datasources;

import java.util.Collection;

import javax.sql.DataSource;

import org.javautil.datasources.DatasourceType;

public interface DataSources {

	public abstract DataSource getDataSource(final String dataSourceName);

	public abstract AbstractDataSource getDataSource(
			final String dataSourceName, final boolean testConnection);

	public abstract Collection<String> getDatasourceNames();

	public abstract DatasourceType getDataSourceType(final String dataSourceName);

}