package org.javautil.sales.populate;

import java.io.File;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.javautil.persist.hibernate.H2InMemorySessionFactory;
import org.javautil.persist.hibernate.TransactionHelper;

public class DataPopulator extends TransactionHelper {
	
	public static final String HBM_DIR = "src/main/resources";
	
	private TransactionHelper txh;
	
	private static SessionFactory sessionFactory;
	
	private Logger logger = Logger.getLogger(getClass());

	private ProductGenerator productGenerator = new ProductGenerator();
	
	private ProductPopulator productPopulator = new ProductPopulator();

	private CustomerGenerator customerGenerator;
	
	private CustomerPopulator customerPopulator = new CustomerPopulator();
	
	private SalespersonGenerator salespersonGenerator = new SalespersonGenerator();
	
	private SalespersonPopulator salespersonPopulator = new SalespersonPopulator();
	
	private DataPopulatorArguments arguments = new DataPopulatorArguments();

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public DataPopulatorArguments getArguments() {
		return arguments;
	}

	public void setArguments(DataPopulatorArguments arguments) {
		this.arguments = arguments;
	}

	public void processArguments(String[] args) {
		arguments.processArguments(args);
	}	

	public void init() throws Exception {
		
		txh = new TransactionHelper();
		txh.setSessionFactory(this.getSessionFactory());
		txh.afterPropertiesSet();		
	}
	
	/**
	 * The Hibernate way.
	 * 
	 * HQL "queries"  using the Class name , lower case would not have worked;
	 * 
	 * Database independent but generates lots of redo and undo.
	 */
	public void clean() {
		Session session = txh.getSession();	
		txh.startTransaction();		
		int saleDeleteCount = session.createQuery("delete Sale").executeUpdate();
		int customerDeleteCount = session.createQuery("delete Customer").executeUpdate();
		int productDeleteCount = session.createQuery("delete Product").executeUpdate();
		int salespersonDeleteCount = session.createQuery("delete Salesperson").executeUpdate();
		logger.debug("deleted sales " + saleDeleteCount + " customer " + customerDeleteCount + " product " + productDeleteCount + " sales "  + salespersonDeleteCount);
	}
	
	// todo test that these arguments are all used
	public void populate() {
		clean();
		
		salespersonGenerator.setQuantityToGenerate(arguments.getSalespersonsCount());
		salespersonPopulator.populate(txh, salespersonGenerator);
		
		customerGenerator = new CustomerGenerator(salespersonGenerator);
		customerPopulator.setNumberOfCustomersToPopulate(arguments.getCustomerCount());
		customerPopulator.populateCustomers(txh,customerGenerator);
		
		productPopulator.setProductCount(arguments.getProductCount());
		productPopulator.populateProducts(txh, productGenerator);
				
		SalesPopulator salesPopulator = new SalesPopulator();
		salesPopulator.setSalesCount(arguments.getSalesCount());
		salesPopulator.populateSales(txh, productGenerator, customerGenerator);
		
		txh.commit();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DataPopulator dp = new DataPopulator();
		BasicConfigurator.configure();			
		sessionFactory = (SessionFactory) H2InMemorySessionFactory.getInstance(new File(HBM_DIR));		
		dp.processArguments(args);
		try {
			dp.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dp.populate();
	}

	protected Serializable doWorkInCurrentTransaction(Connection conn,
			String sql) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
