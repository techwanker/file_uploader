package org.javautil.persist.hibernate.cfg;

import javax.sql.DataSource;

public class DatasourceConfiguration extends AbstractDefaultConfiguration {

	public DatasourceConfiguration(DataSource datasource) {
		super(new DatasourceSettingsFactory(datasource));
	}

}
