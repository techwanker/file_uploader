package com.insightpartners.objectmetadata;

import java.util.HashMap;

public class ObjectTypes {
	/**
	 * ObjectTypeMetaData keyed by Object Id.
	 */
	private final HashMap<ObjectTypeId, ObjectTypeMetadata> objectMetaById = new HashMap<ObjectTypeId, ObjectTypeMetadata>();

	public void process(RawObject raw) {
		ObjectTypeId id = raw.getId();
		ObjectTypeMetadata objectTypeMeta = objectMetaById.get(id);
		objectTypeMeta.process(raw);
	}
}
