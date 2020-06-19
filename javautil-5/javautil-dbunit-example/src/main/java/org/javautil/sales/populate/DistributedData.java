package org.javautil.sales.populate;

import org.javautil.lang.AsString;

public class  DistributedData <ID> {

	private ID id;
	
	private float distributionFactor;

	
	private double cumulativeDistribution;
	
	private double distributionPct;
	
	private int referenceCount;

	public double getCumulativeDistribution() {
		return cumulativeDistribution;
	}

	public void setCumulativeDistribution(double cumulativeDistribution) {
		this.cumulativeDistribution = cumulativeDistribution;
	}

	public float getDistributionFactor() {
		return distributionFactor;
	}

	public void setDistributionFactor(float distributionFactor) {
		this.distributionFactor = distributionFactor;
	}

	public void setId(ID id) {
		this.id = id;
	}
	
	public ID getId() {
		return id;
	}
	
	public int getReferenceCount() {
		return referenceCount;
	}

	public void setReferenceCount(int referenceCount) {
		this.referenceCount = referenceCount;
	}

	public void incrementReference() {
		referenceCount++;
		
	}
	
	public String toString() {
		return new AsString().toString(this);
//		return "mfrId " + upcMfrId + " distributionFactor " + distributionFactor + " cumm " + cumulativeDistribution + " " +
//				" referenceCount " + "distributionPct "+ distributionPct + " " + referenceCount;
	}

	public double getDistributionPct() {
		return distributionPct;
	}

	public void setDistributionPct(double distributionPct) {
		this.distributionPct = distributionPct;
	}
	
}
