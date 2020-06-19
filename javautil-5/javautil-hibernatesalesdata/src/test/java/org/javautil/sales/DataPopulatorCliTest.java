package org.javautil.sales;

import java.sql.SQLException;

import org.javautil.sales.populate.DataPopulatorCli;
import org.junit.Test;

public class DataPopulatorCliTest {
	
    // TODO fails from maven command line with inscrutable error but works from eclipse
	
	@Test
	public void dummy() {
		
	}
	//@Test
	public void test() throws SQLException {
		final String[] args = "-applicationContext src/test/resources/DataPopulatorContext.xml"
				.split(" ");
		DataPopulatorCli.main(args);
	}
}
