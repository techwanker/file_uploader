package org.javautil.sqlrunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SqlInputReaderTest {
	SqlInputReader reader = new SqlInputReader(System.in);
	
	@Test
	public void testSql() {
		String text =  "--! sql; joe  ";
		assertTrue(reader.isSqlSection(text));
		assertEquals("joe",reader.getSqlSectionName(text));
		
	}
}
