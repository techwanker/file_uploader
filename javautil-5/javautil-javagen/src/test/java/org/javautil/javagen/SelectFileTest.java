package org.javautil.javagen;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.javautil.dataset.ColumnMetadata;
import org.javautil.dataset.DatasetMetadata;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;
import org.junit.Before;
import org.junit.Test;

public class SelectFileTest {
	private final DataSources dataSources = new SimpleDatasourcesFactory(
			"../DataSources.xml");

	private DataSource ds;

	private File selectFile;

	@Before
	public void before() {
		ds = dataSources.getDataSource("oracle");
		selectFile = new File("src/test/resources/foreign_keys.sql");
	}

	@Test
	public void test() throws SQLException, IOException {
		Connection conn = ds.getConnection();
		SelectFile sf = new SelectFile(conn, selectFile);
		DatasetMetadata meta = sf.getDatasetMetadata();
		assertNotNull(meta);
		ColumnMetadata cm = meta.getColumnMetaData("TABLE_NAME");
		assertNotNull(cm);
		conn.close();
	}

}
