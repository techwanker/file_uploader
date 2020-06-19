package org.javautil.sales.populate;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;
// todo this isn't persisetn
public class ManufacturerGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4359190832531524L;

	private static final long seed = 4359111110347931524L;
	private HashMap<Integer, Manufacturer> mfrById = new HashMap<Integer,Manufacturer>();

	private TreeMap<Double,Manufacturer> byCumulativeDistribution = new TreeMap<Double,Manufacturer> ();
	
	private Logger logger = Logger.getLogger(getClass());
	
	private int numberOfManufacturers = 71;
	
	private Random random;
	

	
	private static final int k100 = 100000; // 100 k
	
	/**
	 * The range of ratios of manufacturer references.
	 * 
	 * todo describe in detail
	 */
	private static final int distributionFactorRange = 1000;
	
	
	// todo need to create a get Product that has a Pareto Distribution 
	
	public ManufacturerGenerator() {
		random = new Random();
		random.setSeed(seed);
	}
	
	public void generateManufacturers() {
		
		while (mfrById.size() < numberOfManufacturers) {
		
			int mfrId =  random.nextInt(k100);
			
			if (mfrById.get(mfrId) != null) {
				continue;
			}

			Manufacturer  mfr = new Manufacturer();
			mfr.setUpcMfrId(mfrId);
			
			int factor = random.nextInt(distributionFactorRange);
			mfr.setDistributionFactor(factor);
			mfrById.put(mfrId, mfr);
			
		}
		setCumulativeDistribution();
	}
	
	
	/**
	 * Generate the cumulative distribution.
	 * 
	 * The cumulative distribution serves as range boundary in a histogram of values in the dom
	 * todo review
	 * 

	 * The cumulative distribution is a number between 0 and 1 todo inclusive?
	 * of the values that 
	 */
	private void setCumulativeDistribution() {
		double factorSum = 0;
		TreeSet<Manufacturer> manny = new TreeSet<Manufacturer>(new DistributionComparator());
		for (Manufacturer man : mfrById.values()) {
			manny.add(man);
		}
		
		for (Manufacturer man : manny) {
			factorSum += man.getDistributionFactor();
		}
		
		double cumulativeDistribution = 0;
		for (Manufacturer man : manny) {
			double distributionPct = man.getDistributionFactor() / factorSum;
			cumulativeDistribution +=  distributionPct;
			man.setDistributionPct(distributionPct * 100);
			man.setCumulativeDistribution(cumulativeDistribution);
			byCumulativeDistribution.put(new Double(cumulativeDistribution), man);
		}
		logger.debug(" byCumulativeDistribution.size " + byCumulativeDistribution.size());
	}

	/**
	 * 
	 * todo document
	 * 
	 * Returns a manufacturer a percent of the time specified by the randomly determined distribution factor
	 * @return
	 */
	public Manufacturer getManufacturer() {
		double histo = Math.abs(random.nextDouble()) ;
		Manufacturer mfr = byCumulativeDistribution.ceilingEntry(histo).getValue();
		return mfr;
	}

	

	public TreeMap<Double,Manufacturer> getManufacturersOrderedByCumm() {
		return byCumulativeDistribution;
	}
	
	/**
	 * todo doc
	 * @author jjs
	 *
	 */
	
	class DistributionComparator implements Comparator<Manufacturer> {

		@Override
		public int compare(Manufacturer arg0, Manufacturer arg1) {
			int retval = (int) (arg0.getDistributionFactor() - arg1.getDistributionFactor());
			if (retval == 0) {
				retval = arg0.getUpcMfrId() - arg1.getUpcMfrId();
			}
			return retval;
		}
		
	}
}
