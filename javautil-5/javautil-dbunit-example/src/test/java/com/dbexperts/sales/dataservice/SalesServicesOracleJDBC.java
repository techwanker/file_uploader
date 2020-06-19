package com.dbexperts.sales.dataservice;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.javautil.jdbc.datasources.DatasourcesFactory;
import org.javautil.lang.AsString;
import org.javautil.sales.Salesperson;
import org.springframework.context.support.ClassPathXmlApplicationContext;


//

public class SalesServicesOracleJDBC extends AbstractJdbcSalesService implements SalesServicesInterface {

	private AsString asString = new AsString();

	private DataSource dataSource;



	private static final String newline = System.getProperty("line.separator");

	private Logger logger = Logger.getLogger(getClass());

	public void init(ClassPathXmlApplicationContext context) {
		// todo test and document
		// should really inject a good datasource
		try {
			dataSource = DatasourcesFactory.getDataSource("sales");
			
			setConnection(dataSource.getConnection());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// todo show how to conditionally update what session awareness means on
	// these things

	// todo add parm to query
	@SuppressWarnings("unchecked")
	public List<Salesperson> getSalesPeople() {
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
			ps = getConnection().prepareStatement(queryText);
			rset = ps.executeQuery();

			salespersons = new ArrayList<Salesperson>();
			// todo write a salesperson DAO that gets updated
		
			while (rset.next()) {
				Salesperson sp = fetchSalespersonFromResultSet(rset);	
				salespersons.add(sp);
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
		// what happened in my session to the salepersons that just went out of
		// scope? should evict
		return salespersons;
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

	// todo populate an ARRAY do a CAST 

}
