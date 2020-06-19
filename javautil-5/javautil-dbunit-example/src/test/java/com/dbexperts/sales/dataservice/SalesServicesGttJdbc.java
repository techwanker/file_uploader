package com.dbexperts.sales.dataservice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.javautil.oracle.OracleConnectionUtil;
import org.javautil.sales.Customer;
import org.javautil.sales.Salesperson;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SalesServicesGttJdbc extends AbstractJdbcSalesService implements SalesServicesInterface{
	
	private Connection conn;
	
	public SalesServicesGttJdbc(Connection conn) {
		if (conn == null) {
			throw new IllegalArgumentException("conn is null");
		}
		this.conn = conn;
	}
	
	private void cleanNumbers() throws SQLException {
		String deleteText = "delete from gtt_number";
		Statement s = conn.createStatement();
		s.execute(deleteText);
	}
	
	private void populateNumbers(Collection<Number> numbers) throws SQLException {
		cleanNumbers();
		String insertText = "insert into gtt_number(nbr) values (:v) ";
		PreparedStatement ps = conn.prepareStatement(insertText);
		for (Number number : numbers) {
			ps.setObject(1, number);
			ps.addBatch();
		}
		ps.executeBatch();
	}
	

	
	public Set<Customer> getCustomers(Collection<Salesperson> salespeople) throws SQLException {
		Salesperson[] persons = salespeople.toArray(new Salesperson[0]);
		return getCustomers(persons);
	}
	
	public Set<Customer> getCustomers(Salesperson ... salespersons ) throws SQLException {
		LinkedHashSet<Customer> customers = new LinkedHashSet<Customer>();
		
		String columns = "CUSTOMER_ID, CUSTOMER_STATUS, NAME, ADDR_1, ADDR_2, CITY, STATE, ZIP_CD, OUTSIDE_SALESPERSON_ID, INSIDE_SALESPERSON_ID ";
	
		String sqlText = "select " + columns + //
			             "from customer " + //
			             "where outside_salesperson_id in (select nbr from gtt_number) " + //
			             "      or inside_salesperson_id in (select nbr from gtt_number)  ";
		
		Collection<Number> salesids = new ArrayList<Number>();
		for (int i = 0; i < salespersons.length; i++) {
			salesids.add(salespersons[i].getSalespersonId());
		}
		
		populateNumbers(salesids);
			
		try {
			
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery(sqlText);
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
	
	public static void main(String[] args) throws SQLException {
		SalesPersonJDBC sp = new SalesPersonJDBC();
		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("application-context.xml");
		sp.init(springContext);
		Connection conn = sp.getConn();
		SalesServicesGttJdbc gtt = new SalesServicesGttJdbc(conn);
		OracleConnectionUtil ocu = new OracleConnectionUtil(conn);
		ocu.traceOn(gtt.getClass().getSimpleName());
//		ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("application-context.xml");
//		sp.init(springContext);
		List<Salesperson> salespeople = sp.getSalesPeople("C");
	//	sp.getCustomers(salespeople);
//		Connection conn = sp.getConn();
//		GetSalespersonsUsingGTT gtt = new GetSalespersonsUsingGTT(conn);
		gtt.getCustomers(salespeople);
		
	}

	@Override
	public List<Salesperson> getSalesPeople(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Salesperson getSalesperson(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
