package com.dbexperts.sales.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

import org.apache.log4j.Logger;
import org.javautil.lang.AsString;
import org.javautil.sales.BeanTags;
import org.javautil.sales.Customer;
import org.javautil.sales.Salesperson;
import org.springframework.context.support.ClassPathXmlApplicationContext;

// todo must be able to get an OracleConnection

public class SalesPersonJDBC extends AbstractJdbcSalesService implements SalesServicesInterface {

	private AsString asString = new AsString();
	
	private static final String selectColumns = "	salesperson_id, display_name, first_name, last_name ";
	
	static final String queryText = "" + //
	"select " + //
	   selectColumns + //
	"from salesperson ";

	private DataSource dataSource;

	private Connection conn;

	private static final String newline = System.getProperty("line.separator");

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * 
	 * todo Have to be able to set the Oracle Connection properties at the Datasource Level
	 * @param context
	 */
	public void init(ClassPathXmlApplicationContext context) {
		try {
			dataSource =  (DataSource) context.getBean(BeanTags.DATA_SOURCE);
			conn = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	private void getSalesPersonWithCustomersUsingARRAY() {
		
	}
	
	private void getSalesPersonWithCustomersUsingGlobalTemporaryTable() {
		
	}
	
	/**
	 * 
	 */
	private List<Salesperson> getSalespersonWithCustomers() {
		List<Salesperson> salespeople = getSalesPeople("C");
		for (Salesperson sp : salespeople) {
			// todo need a link in the salesperson for the customer
			getCustomers(sp);
			// sp.setCustomer...
		}
		return salespeople;
	}
	
	private Salesperson getSalesperson(ResultSet rset) throws SQLException {
	
		Salesperson salesperson = new Salesperson();
		salesperson.setSalespersonId(rset.getInt("salesperson_id"));
		salesperson.setDisplayName(rset.getString("display_name"));
		salesperson.setFirstName(rset.getString("first_name"));
		salesperson.setLastName(rset.getString("last_name"));
		return salesperson;
	}
	// todo show how to conditionally update what session awareness means on
	// these things

	// todo add parm to query
	// todo make sure that we turn on statement caching in the connection
	@SuppressWarnings("unchecked")
	public List<Salesperson> getSalesPeople(String firstName) {
		// this wierd format works well with eclipse source formatters
		String queryText = "" + //
				"select " + //
				"	salesperson_id, display_name, first_name, last_name " + //
				"from salesperson " + //
				"where first_name < :firstName";

		PreparedStatement ps = null;
		ArrayList<Salesperson> salespersons;
		ResultSet rset;
		try {
			ps = conn.prepareStatement(queryText);
			ps.setString(1,firstName);
			rset = ps.executeQuery();

			salespersons = new ArrayList<Salesperson>();
			// todo write a salesperson DAO that gets updated
			while (rset.next()) {
				salespersons.add(getSalesperson(rset));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		String message = asString.toString(salespersons);
		// todo work on better presentation
		System.out.println(newline + salespersons);
		return salespersons;
	}
	

	public Set<Customer> getCustomers(Collection<Salesperson> salespeople) {
		Salesperson[] persons = salespeople.toArray(new Salesperson[0]);
		return getCustomers(persons);
	}

	
	public Set<Customer> getCustomers(Salesperson ... salespersons ) {
		LinkedHashSet<Customer> customers = new LinkedHashSet<Customer>();
		
		String columns = "CUSTOMER_ID, CUSTOMER_STATUS, NAME, ADDR_1, ADDR_2, CITY, STATE, ZIP_CD, OUTSIDE_SALESPERSON_ID, INSIDE_SALESPERSON_ID ";
	
		String sqlText = "select " + columns + //
			             "from customer " + //
			             "where outside_salesperson_id in table(cast :salesperson as number_array) " + //
			             "      or inside_salesperson_id in table(cast :salesperson  as number_array) ";
		
		int[] salesids = new int[salespersons.length];
		for (int i = 0; i < salespersons.length; i++) {
			salesids [i] = salespersons[i].getSalespersonId();
		}
		
		ARRAY salesidArray; 
		
		
		try {
	
			final ArrayDescriptor NUMBER_ARRAYDescriptor = ArrayDescriptor.createDescriptor("NUMBER_ARRAY", conn);
			salesidArray = new ARRAY(NUMBER_ARRAYDescriptor, conn, salesids);
			
			
			PreparedStatement stmt = conn.prepareStatement(sqlText);
			stmt.setObject(1, salesidArray);
			stmt.setObject(2, salesidArray);
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				Customer cust = populateCustomer(rset);
				customers.add(cust);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return customers;

		
	}
	
//	private Customer populateCustomer(ResultSet rset) throws SQLException {
//		rset.next();
//			Customer cust = new Customer();
//			cust.setCustomerId(rset.getInt(1));
//			cust.setCustomerStatus(rset.getString(2));
//			cust.setName(rset.getString(3));
//			cust.setAddr1(rset.getString(4));
//			cust.setAddr2(rset.getString(5));
//			cust.setCity(rset.getString(6));
//			cust.setState(rset.getString(7));
//			cust.setZipCd(rset.getString(8));
//			cust.setOutsideSalespersonId(rset.getInt(9));
//			
//			Salesperson sp = new Salesperson();
//			sp.setSalespersonId(rset.getInt(10));
//			cust.setSalesperson(sp);
//			//cust.setInsideSalespersonId(rset.getInt(10));
//			return cust;
//	}
	
	public Set<Customer> getCustomers(Salesperson salesperson) {
		LinkedHashSet<Customer> customers = new LinkedHashSet<Customer>();
		String sqlText = "select CUSTOMER_ID, CUSTOMER_STATUS, NAME, ADDR_1, ADDR_2, CITY, STATE, ZIP_CD, OUTSIDE_SALESPERSON_ID, INSIDE_SALESPERSON_ID " +
			             "from customer where outside_salesperson_id = :salesperson or inside_salesperson_id = :salesperson ";
		
		try {
			PreparedStatement stmt = conn.prepareStatement(sqlText);
			stmt.setInt(1, salesperson.getSalespersonId());
			stmt.setInt(2, salesperson.getSalespersonId());
			ResultSet rset = stmt.executeQuery();
			while (rset.next()) {
				Customer cust = populateCustomer(rset);

				customers.add(cust);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return customers;

		
	}
	// todo write a table cast
	
	// todo populate gtt and try different levels of sampling
	@Override
	public Salesperson getSalesperson(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	// todo populate an ARRAY do a CAST 
	
	public static void main(String[] args) {
		SalesPersonJDBC sp = new SalesPersonJDBC();
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("application-context.xml");
		sp.init(springContext);
		List<Salesperson> salespeople = sp.getSalesPeople("C");
		sp.getCustomers(salespeople);
	}

	public Connection getConn() {
		return conn;
	}

	@Override
	public int getCustomerCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getProductCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSalesCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSalespersonCount() {
		// TODO Auto-generated method stub
		return 0;
	}

}
