package org.javautil.sales.populate;

import org.apache.log4j.Logger;
import org.javautil.persist.hibernate.TransactionHelper;
import org.javautil.sales.Product;

public class ProductPopulator {
	private Logger logger = Logger.getLogger(getClass());
	/**
	 * Number of products to generate;
	 */
	private int productCount = 1000;
	
	private int batchSize = 100;
	
	
	
	public ProductGenerator populateProducts(TransactionHelper txh, ProductGenerator productGenerator) {
		long beforeInsert = System.currentTimeMillis();

		for (int i = 0 ; i < productCount; i++) {
			Product p = productGenerator.generateProduct();
			if (i > 0 && i % batchSize == 0) {
				logger.debug("product records created " + i);
				txh.flush();  // todo figure out batching
			}
			txh.save(p);
		}

		long afterInsert = System.currentTimeMillis();
		// todo change back to logger messags
		System.out.println("elapsed Millis " + (afterInsert - beforeInsert));
		txh.flush();
		return productGenerator;

	}


	public void setProductCount(final int _productCount) {
		productCount = _productCount;
		
	}


	public int getBatchSize() {
		return batchSize;
	}


	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
}
