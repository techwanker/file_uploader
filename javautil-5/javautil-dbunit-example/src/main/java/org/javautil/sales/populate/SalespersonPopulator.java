package org.javautil.sales.populate;

import org.apache.log4j.Logger;
import org.javautil.persist.hibernate.TransactionHelper;
import org.javautil.sales.Salesperson;

/**
 * Populates the sale table.
 * @author jjs
 *
 */
public class SalespersonPopulator {
	private Logger logger = Logger.getLogger(getClass());

	public void populate(TransactionHelper txh, SalespersonGenerator salespersonGenerator) {
		logger.info("adding sales");
		for (Salesperson sp : salespersonGenerator.getSalesPeople()) {
			txh.save(sp);
		}
		// flushing fills in the Generated Keys -- todo confirm at flush
		// todo write up on Oracle Sequence generator
		txh.flush();
	}

}
