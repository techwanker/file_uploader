package com.dbexperts.persist.hibernate;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

/**
 * This hibernate reveng strategy overrides default strategy.
 * 
 */
public class RevEngStrategy extends DelegatingReverseEngineeringStrategy {

	Logger logger = Logger.getLogger(getClass());
	public RevEngStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	//	System.out.println("*******************************************");

	}

	/**
	 * Set default generation strategy to be sequence.
	 * 
	 * todo this should use the oracle sequence generator
	 */
	@Override
	public String getTableIdentifierStrategyName(TableIdentifier ti) {
		String configuredName = super.getTableIdentifierStrategyName(ti);
		if (configuredName == null) {
			configuredName = "sequence";
		}

		System.out.println("configuredName for " + ti.getName() + " is " + configuredName);


		return configuredName;
	}

	/**
	 * Set sequence generation strategy properties. 
	 * 
	 * Sets the sequence name by appending "_SEQ" to the table name.
	 * 
	 * For example 
	 * 
	 * <ul>
	 *   <li>Table Name: customer</li>
	 *   <li>Primary key column name: customer_id</li>
	 *   <li>Source sequence: customer_seq</li>
	 * </ul>
	 *   
	 */
	public Properties getTableIdentifierProperties(TableIdentifier ti) {
		Properties props = super.getTableIdentifierProperties(ti);
		if (props == null) {
			props = new Properties();
			props.put("sequence", ti.getName() + "_SEQ");
		}
		return props;
	}

}
