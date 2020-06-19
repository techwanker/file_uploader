package com.dbexperts.sales.dataservice;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.javautil.lang.AsString;
import org.javautil.sales.BeanTags;
import org.javautil.sales.Customer;
import org.javautil.sales.GttNumber;
import org.javautil.sales.Salesperson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dbexperts.persist.hibernate.Dao;
import com.dbexperts.persist.hibernate.Persistence;

//

public class SalesServiceHQL implements SalesServicesInterface {
	
	private AsString asString = new AsString();
	
	// todo this seems very contrived
	@Autowired
	private  Persistence daoFactory;
	
	private static final String newline = System.getProperty("line.separator");
	
	private Logger logger = Logger.getLogger(getClass());
	
	private List<Salesperson> salesPeople;
	
	/**
	 * A an example HQL (HibernateQueryLanguage) query.
	 * 
	 * Note that the table names are specified by the mapped Class name, hence Customer 
	 * must be in upper case.  Further the table column names are specified as in SQL.
	 * 
	 * The columns to be selected are implied so the select... is not necessary.
	 */
	private static final String hqlText = 
        "from Customer " + //
        "where outside_salesperson_id in (select nbr from GttNumber) " + //
        "      or inside_salesperson_id in (select nbr from GttNumber)  ";
	
	
	public static void main(String[] args) {
		SalesServiceHQL qe = new SalesServiceHQL();
		BasicConfigurator.configure();
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("application-context.xml");
	
		qe.init(springContext);
		List<Salesperson> salespeople = qe.getSalesPeople("C");
		
		qe.getCustomers(salespeople);
		
	
	}
	
	// todo jjs more contrive 
	public void init(ClassPathXmlApplicationContext context) {
		daoFactory =(Persistence) context.getBean(BeanTags.DAO_FACTORY);
	}
	
	
	/**
	 * Note: Salesperson is the java object and first_name is the database column name 
	 that is todo ... in the object the syntax is a little bizzare
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<Salesperson> getSalesPeople(String firstName) {
		daoFactory.beginTransaction();  // todo think about transaction management
		

		Query q = daoFactory.getSession().createQuery(
				" from Salesperson" +  //
				" where first_name < :firstName ") ;
		q.setString("firstName", firstName);
			// is this now a proxied class? No, provide
		salesPeople  = q.list();
		
		String message = asString.toString(salesPeople);
		// todo work on better presentation
		System.out.println(newline + message);
		// what happened in my session to the salepersons that just went out of scope?  should evict
		 return salesPeople;
	}
	
	public List<Salesperson> getSalesPeople(String lowerLastName, String upperLastName) {
		daoFactory.beginTransaction();  // todo think about transaction management
		

		Query q = daoFactory.getSession().createQuery(
				" from Salesperson" +  //
				" where upper(last_name) >= :lowerLastName " + 
				" and upper(last_name) <=  :upperLastName) ");
				
		q.setString("lowerLastName", lowerLastName);
		q.setString("upperLastName",upperLastName);
			// is this now a proxied class? No, provide
		salesPeople  = q.list();
		
		String message = asString.toString(salesPeople);
		// todo work on better presentation
		System.out.println(newline + message);
		// what happened in my session to the salepersons that just went out of scope?  should evict
		 return salesPeople;
	}


	@Override
	public Salesperson getSalesperson(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Set<Customer> getCustomers(Collection<Salesperson> salespeople) {
		Salesperson[] persons = salespeople.toArray(new Salesperson[0]);
		return getCustomers(persons);
	}
	
	/**
	 * Delete everything in the gtt_number table.
	 * 
	 * An easy, but expensive way to do a delete.
	 * 
	 * Todo check to see what kind of sql this generates.
	 */
	void emptyGttNumber() {
		String hqlText = "from GttNumber";
		Query q = daoFactory.getSession().createQuery(hqlText);
		q.list().clear();
	}
	
	
	public Set<Customer> getCustomers(Salesperson ... salespersons )  {
		LinkedHashSet<Customer> customers = new LinkedHashSet<Customer>();
	
		emptyGttNumber();

		Dao<GttNumber,?> dao = daoFactory.get(GttNumber.class);
		for (int i = 0; i < salespersons.length; i++) {
		
			GttNumber gn = new GttNumber();
			gn.setNbr(salespersons[i].getSalespersonId());
			
			dao.save(gn);
			
		}
		daoFactory.flush();  // push the level 1 cache to the database

		Query q = daoFactory.getSession().createQuery(hqlText);
		customers.addAll(q.list());
				
		return customers;

		
	}

	
	


	@Override
	public int getCustomerCount() {
		return getTableCount("customer");
	}


	@Override
	public int getProductCount() {
		return getTableCount("product");
	}


	@Override
	public int getSalesCount() {
		return getTableCount("sale");
	}


	@Override
	public int getSalespersonCount() {	
		return getTableCount("salesperson");
	}

	public int getTableCount(String tableName) {
		daoFactory.beginTransaction();
		// todo this is not associated with any persistence, why do I need a transaction?
		SQLQuery q   = daoFactory.getSession().createSQLQuery("select count(*) from " + tableName).
		addScalar("COUNT(*)",Hibernate.INTEGER);
		Integer c = (Integer) q.list().get(0);
	return c.intValue();
	}
	
	

	


	public Persistence getDaoFactory() {
		return daoFactory;
	}


	public void setDaoFactory(Persistence daoFactory) {
		this.daoFactory = daoFactory;
	}





}
