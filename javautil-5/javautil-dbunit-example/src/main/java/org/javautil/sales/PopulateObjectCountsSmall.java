package org.javautil.sales;


public class PopulateObjectCountsSmall implements PopulateObjectCounts  {

	public static final	int nSalesperson = 3;
	public static final int nCustomer = 5;
	public static final int nManufacturer = 7;
	public static final int nSales = 100;
	public static final	int nProduct = 20;

	
	
	/* (non-Javadoc)
	 * @see com.dbexpert.sales.PopulateObjectCountsInterface#getNSalesperson()
	 */
	public int getNSalesperson() {
		return nSalesperson;
	}
	/* (non-Javadoc)
	 * @see com.dbexpert.sales.PopulateObjectCountsInterface#getNCustomer()
	 */
	public int getNCustomer() {
		return nCustomer;
	}
	/* (non-Javadoc)
	 * @see com.dbexpert.sales.PopulateObjectCountsInterface#getNManufacturer()
	 */
	public  int getNManufacturer() {
		return nManufacturer;
	}
	/* (non-Javadoc)
	 * @see com.dbexpert.sales.PopulateObjectCountsInterface#getNSales()
	 */
	public  int getNSales() {
		return nSales;
	}
	/* (non-Javadoc)
	 * @see com.dbexpert.sales.PopulateObjectCountsInterface#getNProduct()
	 */
	public  int getNProduct() {
		return nProduct;
	}
}
