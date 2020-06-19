package org.javautil.sales.populate;

import java.util.Date;

import org.javautil.util.DateHelper;

//import org.javautil.calendar.DateHelper;

public class RandomDateGenerator {

	private Date minDate;
	
	private Date maxDate;
	
	private int daysInRange;
	
	// todo jjs allow for work days , week of and assigning randomly based on work days
	/**
	 * 
	 * @param minDate
	 * @param maxDate
	 */
	public RandomDateGenerator(Date minDate, Date maxDate) {
		if (minDate == null) {
			throw new IllegalArgumentException("minDate is null");
		}
		if (maxDate == null) {
			throw new IllegalArgumentException("minDate is null");
		}
		this.minDate = minDate;
		this.maxDate = maxDate;
		this.daysInRange = DateHelper.daysBetween(minDate, maxDate);
	}
	
	public Date getDate() {
		int delta = (int) (Math.random() * daysInRange);
		Date retval = DateHelper.addDays(minDate,delta);
		return retval;
	}
}
