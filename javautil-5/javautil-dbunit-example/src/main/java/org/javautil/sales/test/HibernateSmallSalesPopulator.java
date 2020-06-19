package org.javautil.sales.test;

import org.hibernate.SessionFactory;
import org.javautil.sales.populate.DataPopulator;
import org.javautil.sales.populate.DataPopulatorArguments;
import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author mohamed.jaleel
 */
public class HibernateSmallSalesPopulator implements
		InitializingBean {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void populateData() throws Exception {
		DataPopulatorArguments small = new DataPopulatorArguments();
		small.setCustomerCount(100);
		small.setManufacturerCount(7);
		small.setProductCount(30);
		small.setSalespersonsCount(5);
		small.setSalesCount(500); // bcm added, default was 100k
		DataPopulator dp = new DataPopulator();
		dp.setSessionFactory(this.getSessionFactory());
		dp.setArguments(small);
		dp.init();
		dp.populate();
	}

	public void afterPropertiesSet() throws Exception {
		if (sessionFactory == null) {
			throw new IllegalArgumentException("sessionFactory is null");
		}
		populateData();
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
