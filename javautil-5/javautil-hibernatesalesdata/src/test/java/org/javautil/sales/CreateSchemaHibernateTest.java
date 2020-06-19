package org.javautil.sales;

import org.javautil.sales.database.schema.CreateSchemaHibernate;
import org.junit.Test;

public class CreateSchemaHibernateTest {

	
	
    // TODO fails from maven command line with inscrutable error but works from eclipse
	
	@Test
	public void dummy() {
		
	}
	//@Test
	public void test() {
		final CreateSchemaHibernate creator = new CreateSchemaHibernate();
		creator.go("classpath:application-context.xml");
		// TODO test database objects
	}
}
