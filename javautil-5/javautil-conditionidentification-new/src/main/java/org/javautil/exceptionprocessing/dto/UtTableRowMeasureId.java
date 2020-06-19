package org.javautil.exceptionprocessing.dto;

// Generated Jun 7, 2009 8:20:16 PM by Hibernate Tools 3.2.2.GA

/**
 * UtTableRowMeasureId generated by hbm2java
 */
public class UtTableRowMeasureId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String factTableId;
	private Integer factTablePk;
	private Integer measNbr;
	private Integer meas;

	public UtTableRowMeasureId() {
	}

	public UtTableRowMeasureId(final String factTableId,
			final Integer factTablePk, final Integer measNbr, final Integer meas) {
		this.factTableId = factTableId;
		this.factTablePk = factTablePk;
		this.measNbr = measNbr;
		this.meas = meas;
	}

	public String getFactTableId() {
		return this.factTableId;
	}

	public void setFactTableId(final String factTableId) {
		this.factTableId = factTableId;
	}

	public Integer getFactTablePk() {
		return this.factTablePk;
	}

	public void setFactTablePk(final Integer factTablePk) {
		this.factTablePk = factTablePk;
	}

	public Integer getMeasNbr() {
		return this.measNbr;
	}

	public void setMeasNbr(final Integer measNbr) {
		this.measNbr = measNbr;
	}

	public Integer getMeas() {
		return this.meas;
	}

	public void setMeas(final Integer meas) {
		this.meas = meas;
	}

}