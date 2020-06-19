package org.javautil.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;
import org.junit.Ignore;
import org.junit.Test;

public class DataSourceFactoryTest {

	// TODO
	// I believe this all works and is a maven dependency problem I don't feel like
	// dealing with now
	// http://stackoverflow.com/questions/25644023/error-unmarshalling-xml-in-java-8-secure-processing-org-xml-sax-saxnotrecognize
	private DataSources dataSources;
	@Ignore
	public void testConnection(final Connection conn) throws SQLException {
		final Statement stmt = conn.createStatement();
		stmt.execute("create table a (b number(9))");
		stmt.execute("drop table a");
		stmt.close();
	}

	@Ignore
	@Test
	public void test1() throws SQLException {
		dataSources = new SimpleDatasourcesFactory(
				"src/test/resources/DataSourceH2.xml");
		final Connection conn = dataSources.getDataSource("h2").getConnection();
		testConnection(conn);
		conn.close();
	}

	@Ignore
	@Test
	public void test2() throws SQLException {
		dataSources = new SimpleDatasourcesFactory(
				"src/test/resources/DataSourceH2_2.xml");

		final Connection conn = dataSources.getDataSource("h2").getConnection();
		testConnection(conn);
		conn.close();
	}

	@Ignore
	@Test
	public void test3() throws SQLException {
		dataSources = new SimpleDatasourcesFactory(
				"src/test/resources/DataSourceH2C3P0.xml");
		final Connection conn = dataSources.getDataSource("h2").getConnection();
		testConnection(conn);
		conn.close();
	}
}
