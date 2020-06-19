package com.dbexperts.sales;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.javautil.sales.PopulateObjectCounts;
import org.javautil.sales.PopulateObjectCountsSmall;
import org.javautil.sales.Product;
import org.javautil.sales.Sale;
import org.javautil.sales.Salesperson;
import org.javautil.sales.populate.CustomerGenerator;
import org.javautil.sales.populate.DataPopulator;
import org.javautil.sales.populate.ProductGenerator;
import org.javautil.sales.populate.SalespersonGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.dbexperts.persist.hibernate.Dao;
import com.dbexperts.persist.hibernate.Persistence;
import com.dbexperts.sales.dataservice.SalesServicesOracleJDBC;
import com.dbexperts.sales.examples.CreateDatabaseObjects;
// todo make this some real unit test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
@Transactional
public class DataPopulatorTests {

	@Autowired
	private Persistence daoFactory;

	private Logger logger = Logger.getLogger(getClass());

	private ProductGenerator productGenerator;

	private DataSource datasource;

	private CustomerGenerator customerGenerator;

	private int salesCount = 100;

	public void init(ClassPathXmlApplicationContext context) {
		daoFactory = (Persistence) context.getBean("daoFactory");
		datasource = (DataSource) context.getBean("datasource");
	}

	@Before
	public void Before() throws HibernateException, SQLException {
		BasicConfigurator.configure();
		CreateDatabaseObjects cdo = new CreateDatabaseObjects();
		daoFactory.beginTransaction();
//		cdo.dropObjects(daoFactory.getSession().connection());
//		cdo.createObjects(daoFactory.getSession().connection());
	}
	
	@After
	public void After() {
	}

	
	@Test
	public void addSalesReps() {
		Transaction transaction = daoFactory.getSession().beginTransaction();

		Dao<Salesperson, ?> salespersonDAO = daoFactory.get(Salesperson.class);

		SalespersonGenerator sg = new SalespersonGenerator();
		long beforeInsert = System.currentTimeMillis();
		// todo need to add batch processing
		for (int i = 1; i < 25; i++) {
			Salesperson sp = sg.getSalesperson();
			salespersonDAO.save(sp);
		}
		long afterInsert = System.currentTimeMillis();
		System.out.println("elapsed Millis " + (afterInsert - beforeInsert));

		// don't have an ID yet
		daoFactory.getSession().flush();
		transaction.commit();
	}


	public void addProduct() {
		Session session = daoFactory.getSession();
		Transaction transaction = session.beginTransaction();
		productGenerator = new ProductGenerator();
		long beforeInsert = System.currentTimeMillis();
		// todo need to add batch processing
		// still don't know,

		for (int i = 0; i < salesCount; i++) {
			Product p = productGenerator.generateProduct();
			if (i % 100 == 0) {
				logger.debug("product records created " + i);
				session.flush(); // todo figure out batching

			}
			session.save(p);
		}

		long afterInsert = System.currentTimeMillis();
		System.out.println("elapsed Millis " + (afterInsert - beforeInsert));

		// don't have an ID yet
		daoFactory.getSession().flush();
		transaction.commit();
	}



	public void addSales() {
		Session session = daoFactory.getSession();
		Transaction transaction = session.beginTransaction();
		for (int i = 1; i < salesCount; i++) {
			Sale s = new Sale();
			s.setProduct(productGenerator.getRandomProduct());
			s.setCustomer(customerGenerator.getRandomCustomer());
			// todo create a lognormal distribution
			session.save(s);
			if (i % 1000 == 0) {
				session.flush();
				session.clear();
				logger.info("sales " + i);
			}
		}
		session.flush();
		transaction.commit();
	}

	@Test
	public void testArguments() throws SQLException { 
		PopulateObjectCounts counts = new PopulateObjectCountsSmall();
		
		String argString =
			"-salesp=" + counts.getNSalesperson() + //
			" -customer=" + counts.getNCustomer() + //
			" -p=" + counts.getNProduct() + //
			" -m=" + counts.getNManufacturer() + // 
			" -salesc=" + counts.getNSales(); 
		
		DataPopulator.main(argString.split(" "));
		SalesServicesOracleJDBC ss = new SalesServicesOracleJDBC();
		// without the init this can't be run as a test fix
		// todo fix this
		Connection conn = datasource.getConnection();
		ss.setConnection(conn);
		assertEquals(counts.getNSalesperson(),ss.getSalespersonCount());
		assertEquals(counts.getNCustomer(),ss.getCustomerCount());
		assertEquals(counts.getNSales(),ss.getSalesCount());
		assertEquals(counts.getNProduct(),ss.getProductCount());
	}
	
	// todo there is a way to read arguments as properties 
	/**
	 * What are we measuring here?
	 * 
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		BasicConfigurator.configure();
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("application-context.xml");
		
		DataPopulatorTests dp = new DataPopulatorTests();
		dp.init(springContext);
		dp.testArguments();

	}
}
