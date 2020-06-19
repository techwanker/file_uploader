package com.dbexperts.sales.examples;

import javax.sql.DataSource;

import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.javautil.sales.Customer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dbexperts.persist.hibernate.Persistence;
import com.dbexperts.sales.dataservice.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:application-context.xml" })
public class CustomerServiceClient {
	
	@BeforeClass 
	public static void beforeClass() {
		 BasicConfigurator.configure(); 
	}
    @Test
	public void testServices()
	{  
    	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
    	Persistence daoFactory = (Persistence) context.getBean("daoFactory");
		DataSource datasource = (DataSource) context.getBean("datasource");
		
		/**
		 *   Always get a new session for examples to work. 
		 *   This sis like logging into the oracle forms menu or by logging into TOAD 
		 *   
		 */
    	Session session = daoFactory.getNewSession();  // 1. Session begins , got connected to database .
        
    	CustomerService customerService = new CustomerService();
    	  try { 

    		  for (Customer aCustomer : customerService.ListCustomers(session)) {
                  System.out.println("Customer: " + aCustomer.getCustomerId()+ " Customer Name " );
              }
          } finally {
              // No matter what, close the session
              session.close();
          }

		
	}
	

}
