package com.dbexperts.sales.examples;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.javautil.sales.Salesperson;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.dbexperts.persist.hibernate.Persistence;

/**
 * 
 * todo show how using an Oracle Connection with caching decreases parses
 * @author jjs
 *
 */
//todo make this some real unit test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class ListDelete {
	
	@Autowired
	private Persistence persistence;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@BeforeClass
	public static void beforeClass() {
		BasicConfigurator.configure();
	}
	
	public List<Salesperson> getSalesPeople(String lowerLastName, String upperLastName) {
		persistence.beginTransaction();  // todo think about transaction management
		Query q = persistence.getSession().createQuery(
				" from Salesperson" +  //
				" where upper(last_name) >= :lowerLastName " + 
				" and upper(last_name) <=  :upperLastName) ");
				
		q.setString("lowerLastName", lowerLastName);
		q.setString("upperLastName",upperLastName);
		return q.list();

	}

	/**
	 * The list on an HQL query is not backed .
	 */
	@Test
	public void listDelete() {
		// get some salespersons
		List<Salesperson> salespersons = getSalesPeople("A","D");
		int initialSalespersonsSize = salespersons.size();
		logger.info("salespersons count " + salespersons.size());
		System.out.println("*******************************");
		
		System.out.println("salespersons count " + salespersons.size());
		// remove one from the list
		salespersons.remove(0);
		
		// get them again
		List<Salesperson> salespersons2 = getSalesPeople("A","D");
		assertEquals(initialSalespersonsSize,salespersons2.size());
	
		System.out.println("salespersons2 count " + salespersons2.size());
		// flushing doesn't affect 
		persistence.getSession().flush();
		
		List<Salesperson> salespersons3 = getSalesPeople("A","D");
		assertEquals(initialSalespersonsSize,salespersons3.size());
		System.out.println("salespersons3 count " + salespersons3.size());		
	}

	
	/**
	 * Explicit deletes are not batched.
	 * 
	 * todo query v$wait, etc from another session to eliminate need to show trace file 
	 */
	@Test
	public void explicitDelete() {
		List<Salesperson> salespersons = getSalesPeople("A","D");

		logger.info("salespersons count " + salespersons.size());
		System.out.println("*******************************");
		System.out.println("salespersons count " + salespersons.size());
		persistence.delete(salespersons.get(0));
		// note no flush
		// a query back to the database?  todo demonstrate that happened
		List<Salesperson> salespersons2 = getSalesPeople("A","D");
		assertEquals(salespersons.size() - 1,salespersons2.size());
		System.out.println("salespersons2 count " + salespersons2.size());
		
	}
	

	/**
	 * The list on an HQL query is not backed .
	 * 
	 * todo prove how many executes took place
	 */
	@Test
	public void explicitMultiDelete() {
		List<Salesperson> salespersons = getSalesPeople("A","D");

		logger.info("salespersons count " + salespersons.size());
		System.out.println("*******************************");
		System.out.println("salespersons count " + salespersons.size());
		for (Salesperson salesperson : salespersons) {
			persistence.delete(salesperson);
		}
		List<Salesperson> salespersons2 = getSalesPeople("A","D");
		assertEquals(0,salespersons2.size());
	
	}
	
	
//	}
}
