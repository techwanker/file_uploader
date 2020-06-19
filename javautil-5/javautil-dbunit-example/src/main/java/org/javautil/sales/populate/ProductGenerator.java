package org.javautil.sales.populate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.log4j.Logger;
import org.javautil.sales.Product;

public class ProductGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4359190860347931524L;

	private static final long seed = 4359190860347931524L;
	private HashSet<Integer> upcCodes = new HashSet<Integer>();

	private ArrayList<Product> productList = new ArrayList<Product>();

	private static final int E5 = 100000; // as in 1e5 (fortran and
	// successors) 10^5

	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Number of times a product upc generation resulted in a formerly assigned
	 * value.
	 * 
	 */
	private long collisionCount;

	private int numberOfManufacturers = 701;

	private Random random;
	// todo delete
//	private RandomUtils randomUtils = new RandomUtils();

	public ProductGenerator() {
		random = new Random();
		random.setSeed(seed);
	}

	public String generateUPC() {

		String retval = null;
		int tryCount = 0;
		while (retval == null) {
			int mfr = random.nextInt(numberOfManufacturers);
			int product = random.nextInt(E5);
			int upc = mfr * E5 + product;
			if (!upcCodes.contains(upc)) {
				upcCodes.add(upc);
				retval = Integer.toString(upc);
			} else {
				collisionCount++;
				if (logger.isInfoEnabled()) {
					String upcFormatted = mfr + "-" + product;
					logger.info("already have " + upcFormatted + 
							" tryCount " + tryCount + 
							" collisions " + collisionCount + " "
						+ upcCodes.size());
				}
			}
			tryCount++;
		}
		return retval;
	}

	public Product generateProduct() {
		Product p = new Product();
		p.setUpc10(generateUPC());
		p.setProductStatus("S");
		productList.add(p);
		logger.debug("created " + p.getUpc10());
		return p;
	}

	public Product getRandomProduct() {
		Product product = null;
		if (productList.size() == 0) {
			throw new IllegalStateException("getProduct has not been called");
		}
		int index = (int) Math.random() * productList.size();
		product = productList.get(index);
		if (logger.isDebugEnabled()) {
			logger.debug("returning " + product);
		}
		return product;

	}
}
