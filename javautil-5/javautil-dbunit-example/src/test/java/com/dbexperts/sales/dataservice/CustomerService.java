package com.dbexperts.sales.dataservice;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.javautil.sales.Customer;

public class CustomerService {
    
	public List<Customer> ListCustomers(Session session  )
	{
		Query query = session.createQuery(" from Customer");
		return query.list();
	}
	
	public Customer getCustomerForId(int customerId, Session session)
	{   Query query = session.createQuery(" from Customer where customerId = :cstId");
		query.setInteger("cstId", customerId);		
		return (Customer) query.list();		
	}
	
	public List getCustomerByName(String customerName, Session session)
	{   
		List<Customer> retval  = session.createQuery(" from Customer where name = :cstName").
	    setString("cstName",customerName).list();	
		session.close();
	    return retval;	
	}
}
