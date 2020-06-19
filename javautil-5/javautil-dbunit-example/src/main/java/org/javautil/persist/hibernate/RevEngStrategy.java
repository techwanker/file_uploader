package org.javautil.persist.hibernate;

import java.util.Properties;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;

/**
 * This hibernate reveng strategy overrides default strategy.
 * 
 * @author tim@softwareMentor.com
 */
public class RevEngStrategy extends DelegatingReverseEngineeringStrategy {

	public RevEngStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}

	/**
	 * Set default generation strategy to be sequence.
	 */
	public String getTableIdentifierStrategyName(TableIdentifier ti) {
		String configuredName = super.getTableIdentifierStrategyName(ti);
		if (configuredName == null) {
			configuredName = "sequence";
		}

		return configuredName;
	}

	/**
	 * Set sequence generation strategy properties. Convention: Sequence name is
	 * PK column name + _SEC
	 */
	public Properties getTableIdentifierProperties(TableIdentifier ti) {
		Properties props = super.getTableIdentifierProperties(ti);
		if (props == null) {
			props = new Properties();
			props.put("sequence", ti.getName() + "_NBR_SEQ");
		}
		return props;
	}

}
