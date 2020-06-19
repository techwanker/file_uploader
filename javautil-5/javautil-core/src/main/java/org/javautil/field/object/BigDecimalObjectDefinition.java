package org.javautil.field.object;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

public class BigDecimalObjectDefinition implements ObjectDefinition {

	private final Logger logger = Logger.getLogger(getClass());

	public BigDecimalObjectDefinition() {
	}

	// TODO add ranges for validation
	@Override
	public BigDecimal

			getObject(String externalRepresentation) throws ObjectParseException {
		try {
			return new BigDecimal(externalRepresentation);
		} catch (Exception e) {
			throw new ObjectParseException("error while parsing '" + externalRepresentation + "' ", e);
		}
	}

	// TODO move to abstract class
	@Override
	public boolean validate(String externalRepresentation) {
		boolean retval = true;
		try {
			getObject(externalRepresentation);
		} catch (ObjectParseException ope) {
			retval = false;
		}
		return retval;
	}

}
