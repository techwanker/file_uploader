package org.javautil.field.object;

import org.apache.log4j.Logger;

public class LongObjectDefinition extends AbstractObjectDefinition {

	private final Logger logger = Logger.getLogger(getClass());

	public LongObjectDefinition() {
	}

	// TODO add ranges for validation
	@Override
	public Long getObject(final String externalRepresentation) throws ObjectParseException {
		try {
			return new Long(externalRepresentation);
		} catch (final Exception e) {
			throw new ObjectParseException(externalRepresentation, e);
		}
	}

}
