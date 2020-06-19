package org.javautil.sales.populate;

import org.javautil.commandline.CommandLineHandler;
import org.javautil.commandline.annotations.Optional;

/**
 * Arguments to DataPopulator.
 * 
 * This bean holds the command line values for DataPopulator.  see DataPopulator.
 * @author jjs
 *
 */
public class DataPopulatorArguments {
	
	@Optional
	private Integer salespersonsCount = 100;
	
	@Optional
	private Integer customerCount = 1000;
	
	@Optional
	private Integer productCount = 1000;
	
	@Optional
	private Integer manufacturerCount = 701;
	
	// todo this blows up if this is an int - the assigner does
	@Optional 
	private Integer salesCount = 100000;
	
	
	
	public void processArguments(String [] args) {
		CommandLineHandler cmd = new CommandLineHandler(this);
		cmd.parse(args);
	}

	public int getSalespersonsCount() {
		return salespersonsCount;
	}

	public void setSalespersonsCount(Integer salespersonsCount) {
		this.salespersonsCount = salespersonsCount;
	}

	public int getCustomerCount() {
		return customerCount;
	}

	public void setCustomerCount(Integer customerCount) {
		this.customerCount = customerCount;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
	}

	

	public int getSalesCount() {
		return salesCount;
	}

	public void setSalesCount(Integer salesCount) {
		this.salesCount = salesCount;
	}

	public Integer getManufacturerCount() {
		return manufacturerCount;
	}

	public void setManufacturerCount(Integer manufacturerCount) {
		this.manufacturerCount = manufacturerCount;
	}

	
	
}
