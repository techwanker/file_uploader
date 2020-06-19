package org.javautil.sales.populate;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.javautil.persist.hibernate.TransactionHelper;
import org.javautil.sales.Sale;
import org.javautil.util.Day;

/**
 * Populates the sale table.
 * @author jjs
 *
 */
public class SalesPopulator {
	private Logger logger = Logger.getLogger(getClass());
	
	private int salesCount = 10000;
	private int batchSize = 1000;
	
	
	// todo we have completely failed to abstract from hibernate
	public void populateSales(TransactionHelper txh, ProductGenerator productGenerator, CustomerGenerator customerGenerator) {
		logger.info("adding sales");
		RandomDateGenerator dateGen =new RandomDateGenerator(new Day(2005,01,01), new Day(2008,12,31));
		for (int i = 0 ; i < salesCount; i++) {
			Sale s = new Sale();
			s.setProduct(productGenerator.getRandomProduct());
			s.setCustomer(customerGenerator.getRandomCustomer());
			s.setShipDt(dateGen.getDate());
			double qty = Math.random() * 10000;  // todo should vary by product
			s.setQty(new BigDecimal(qty));
			// todo create a lognormal distribution 
			txh.save(s);
			if ( i % batchSize == 0) {
				txh.flush();
				txh.flush();
				logger.info("sales " + i);
				System.out.println("sales " + i);
			}
		}
	
		txh.flush();
	}

	public int getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(int salesCount) {
		this.salesCount = salesCount;
	}
}
