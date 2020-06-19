package org.javautil.dex;

import java.util.ArrayList;

public class NumberStatistics {

	private int count;
	private int nullCount;
	private double sum;
	private Double min;
	private Double max;

	private final ArrayList<Number> values = new ArrayList<Number>();

	public void addValue(final Number value) {
		values.add(value);
	}

	public void computeMedian() {

	}

	// @todo lookup Collection Sort

	public void computeStats() {
		final int cnt = values.size();
		for (int i = 0; i < cnt; i++) {
			final Number n = values.get(i);
			if (n != null) {
				count++;
				final double d = n.doubleValue();
				sum += n.doubleValue();
				if (min == null || d < min.doubleValue()) {
					min = d;
				}
				if (max == null || d > max.doubleValue()) {
					max = d;
				}
			} else {
				nullCount++;
			}
		}
	}
}
