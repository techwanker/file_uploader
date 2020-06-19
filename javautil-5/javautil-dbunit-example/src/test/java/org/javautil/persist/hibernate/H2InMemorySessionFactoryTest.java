package org.javautil.persist.hibernate;

import java.io.File;
import java.util.Map;

import junit.framework.Assert;

import org.hibernate.InvalidMappingException;
import org.hibernate.SessionFactory;
import org.junit.Test;

public class H2InMemorySessionFactoryTest {

	@Test(expected = InvalidMappingException.class)
	public void testNoMappings() {
		// error because no mappings (*.hbm.xml) are in the project root
		H2InMemorySessionFactory.getInstance(new File("."));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testWithMappings() {
		// success because 7 mappings (*.hbm.xml) are in the specified folder
		SessionFactory sessionFactory = H2InMemorySessionFactory.getInstance(new File(
				"src/main/resources/org/javautil/sales"));
		Map metadata = sessionFactory.getAllClassMetadata();
		Assert.assertEquals(7, metadata.size());
	}

}
