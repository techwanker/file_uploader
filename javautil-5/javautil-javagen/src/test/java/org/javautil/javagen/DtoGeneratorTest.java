package org.javautil.javagen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.javautil.dataset.ColumnAttributes;
import org.javautil.io.StringFileWriter;
import org.junit.Before;
import org.junit.Test;

public class DtoGeneratorTest {
	private final Logger logger = Logger.getLogger(getClass());
	private final ProductTableMeta productTable = new ProductTableMeta();
	private final DtoGenerator generator = new DtoGenerator();

	@Before
	public void before() {
		// System.setProperty("DATASOURCE_FILE",
		// "src/test/resources/DataSources.xml");
		// datasource = DatasourcesFactory.getDataSource("xe");
	}

	@Test
	public void testGetterComments() {
		ColumnAttributes column = productTable.getColumn("PRODUCT_ID");
		assertNotNull(column);
		String actual = generator.getGetterComments(column);
		String expected = "\n\t/** Getter for productId. */\n";
		assertEquals(expected, actual);

	}

	@Test
	public void testSetterComments() {
		ColumnAttributes column = productTable.getColumn("PRODUCT_ID");
		assertNotNull(column);
		String actual = generator.getSetterComments(column);
		String expected = "\n\t/** Setter for productId. */\n";
		assertEquals(expected, actual);
		logger.debug("actual\n" + actual);
	}

	@Test
	public void testFull() throws IOException, SQLException {
		JavaGeneratorArguments args = DaoGeneratorTest.getArguments();
		DaoGeneratorTest dgt = new DaoGeneratorTest();
		dgt.beforeClass();
		String text = generator.getDto(args, dgt.getColumns());
		StringFileWriter.writeClassAsPath("target/actual",
				args.getDtoPackageName(), args.getDtoClassName(), text);
		// TODO
		logger.debug(text);
	}
}
