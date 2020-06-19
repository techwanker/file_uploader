package org.javautil.javagen;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.javautil.dataset.ColumnAttributes;
import org.javautil.dataset.DatasetMetadata;
import org.javautil.dataset.DatasetMetadataFactory;
import org.javautil.file.FileHelper;
import org.javautil.io.StringFileWriter;
import org.javautil.jdbc.datasources.DataSources;
import org.javautil.jdbc.datasources.SimpleDatasourcesFactory;
import org.javautil.jdbc.metadata.Table;
import org.javautil.jdbc.metadata.TableImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DaoGeneratorTest {
	private final Logger logger = Logger.getLogger(getClass());
	private final ProductTableMeta productTable = new ProductTableMeta();
	private static DataSource datasource;
	private static List<ColumnAttributes> columns;
	private static List<String> lines;
	private static final DataSources dataSources = new SimpleDatasourcesFactory(
			"../DataSources.xml");

	public static JavaGeneratorArguments getArguments() {
		JavaGeneratorArguments args = new JavaGeneratorArguments();
		args.setDtoPackageName("org.javautil.oracle.dto");
		args.setDaoPackageName("org.javautil.oracle.dao");
		args.setDaoImplClassName("OracleForeignKeyDaoImpl");
		args.setDtoClassName("OracleForeignKey");
		return args;
	}

	@BeforeClass
	public static void beforeClass() throws SQLException, IOException {
		System.setProperty("DATASOURCES_FILE", "../DataSources.xml");
		datasource = dataSources.getDataSource("xe");
		columns = getColumns();
		lines = FileHelper.getStringArrayList(new File(
				"src/test/resources/foreign_keys.sql"));
	}

	@Before
	public void before() {

	}

	public void after() {
		// need to close datasource
	}

	@Test
	public void getInsertText() {
		DaoGenerator generator = new DaoGenerator();
		String actual = generator.getInsertStatement(productTable);
		logger.debug(actual);
	}

	@Test
	public void getGenerateGetAllStub() {
		JavaGeneratorArguments args = getArguments();
		DaoGenerator generator = new DaoGenerator();
		String actual = generator.generateGetAllStub(args);
		logger.debug(actual);
		// TODO write test
	}

	// @Test
	// public void getClassDeclarationTest() {
	// JavaGeneratorArguments args = getArguments();
	// DaoGenerator generator = new DaoGenerator();
	// String actual = generator.getClassDeclaration(args);
	// logger.debug(actual);
	// }

	static DatasetMetadata getDatasetMetadata() throws SQLException,
			IOException {
		String sql = FileHelper
				.getFileText("src/test/resources/foreign_keys.sql");
		Connection conn = datasource.getConnection();
		PreparedStatement ps = conn.prepareStatement(sql);
		ResultSet rs = ps.executeQuery();
		DatasetMetadata meta = DatasetMetadataFactory.getInstance(rs
				.getMetaData());
		ps.close();
		conn.close();
		return meta;
	}

	public static List<ColumnAttributes> getColumns() throws SQLException,
			IOException {
		return (List<ColumnAttributes>) (Object) getDatasetMetadata()
				.getColumnMetadata();

	}

	@Test
	public void getSelectColumns() throws IOException, SQLException {

		DaoGenerator generator = new DaoGenerator();
		String columnsText = generator.getColumnsForSql(getColumns());
		logger.debug(columnsText);
	}

	@Test
	public void getGetRowByName() {
		JavaGeneratorArguments args = getArguments();
		DaoGenerator generator = new DaoGenerator();
		String columnsText = generator.getGetRowByName(args, columns);
		logger.debug("getRowByName\n" + columnsText);
	}

	@Test
	public void getSetBean() {

		DaoGenerator generator = new DaoGenerator();
		String setBean = generator.getSetBean(columns.get(0));
		logger.debug(setBean);
	}

	@Test
	public void getDao() throws IOException, SQLException {
		JavaGeneratorArguments args = getArguments();
		DaoGenerator generator = new DaoGenerator();

		String text = generator.getDao(args, lines, columns);
		StringFileWriter.writeClassAsPath("target/actual",
				args.getDaoPackageName(), args.getDaoImplClassName(), text);
		logger.debug(text);
	}

	@Test
	public void testSqlFile() {
		JavaGeneratorArguments args = getArguments();
		args.setDatasourceName("oracle");
		args.setSelectFile(new File("src/test/resources/foreign_keys.sql"));
		DaoGenerator generator = new DaoGenerator();
		Table table = new TableImpl();
		String text = generator.getDao(args, table, productTable.getColumns());
		// TODO
		logger.debug(text);
	}
}
