package org.javautil.exceptionprocessing.dto;

// Generated Jun 7, 2009 8:20:16 PM by Hibernate Tools 3.2.2.GA

/**
 * UtTableRuleRptSum generated by hbm2java
 */
public class UtTableRuleRptSum implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer utTableRuleRptSumNbr;
	private UtTableRule utTableRule;
	private String rptDescr;
	private Integer apexPageNbr;

	public UtTableRuleRptSum() {
	}

	public UtTableRuleRptSum(final UtTableRule utTableRule,
			final String rptDescr, final Integer apexPageNbr) {
		this.utTableRule = utTableRule;
		this.rptDescr = rptDescr;
		this.apexPageNbr = apexPageNbr;
	}

	public Integer getUtTableRuleRptSumNbr() {
		return this.utTableRuleRptSumNbr;
	}

	public void setUtTableRuleRptSumNbr(final Integer utTableRuleRptSumNbr) {
		this.utTableRuleRptSumNbr = utTableRuleRptSumNbr;
	}

	public UtTableRule getUtTableRule() {
		return this.utTableRule;
	}

	public void setUtTableRule(final UtTableRule utTableRule) {
		this.utTableRule = utTableRule;
	}

	public String getRptDescr() {
		return this.rptDescr;
	}

	public void setRptDescr(final String rptDescr) {
		this.rptDescr = rptDescr;
	}

	public Integer getApexPageNbr() {
		return this.apexPageNbr;
	}

	public void setApexPageNbr(final Integer apexPageNbr) {
		this.apexPageNbr = apexPageNbr;
	}

}
