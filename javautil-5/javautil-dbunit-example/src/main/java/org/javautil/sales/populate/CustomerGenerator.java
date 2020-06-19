package org.javautil.sales.populate;

import java.util.ArrayList;
import java.util.Random;

import org.javautil.sales.Customer;
import org.javautil.sales.Salesperson;

/**
 * Generates test Customers.
 * 
 * Creates Customer objects for test purposes.
 * @author jjs
 *
 */
public class CustomerGenerator {

	private ArrayList<Customer> customerList = new ArrayList<Customer>();
	private Random random = new Random();
	
	private SalespersonGenerator salesPeople;
	private boolean initialized = false;
	
	public CustomerGenerator(SalespersonGenerator salesPeople) {
		if ( salesPeople == null) {
			throw new IllegalArgumentException("salesPeople is null");
		}
		this.salesPeople = salesPeople;
		
	}
	
	public void init() {
		if (initialized) {
			throw new IllegalStateException("already initialized");
		}
		random.setSeed(31415926535L);
		initialized = true;
	}

	// this version works with a salesperson not as a foreign key
//	public Customer getCustomer() {
//		Customer p  = new Customer();
//		p.setInsideSalespersonId(salesPeople.getRandomSalesperson().getSalespersonId());
//		customerList.add(p);	
//		return p;
//	}

	
	public Customer getCustomer() {
		Customer p  = new Customer();
	
		Salesperson sp = salesPeople.getRandomSalesperson();
		p.setSalesperson(sp);
		//p.setInsideSalespersonId(salesPeople.getRandomSalesperson().getSalespersonId());
		customerList.add(p);	
		return p;
	}
	
	public void generateCustomers(int n) {
		if (n < 0 ) {
			throw new IllegalArgumentException("n is less than 0");
		}
		for (int i = 0 ; i < n ; i++) {
			getCustomer();
		}
	}
	
	public Customer getRandomCustomer() {
		int index =  random.nextInt(customerList.size());
		return customerList.get(index);
	}
}
