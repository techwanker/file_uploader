/**
 * 
 */
package com.insightpartners.objectmetadata.ddl;

import java.util.List;

import com.insightpartners.objectmetadata.ObjectTypeMetadata;

/**
 * @author jjs
 * 
 */
public class CreateTableForObjectType {
	private final ObjectTypeMetadata typeMeta;

	List<String> referentialConstraints;

	public CreateTableForObjectType(ObjectTypeMetadata typeMeta) {
		super();
		this.typeMeta = typeMeta;
	}

	public List<String> createTableDdl(ObjectTypeMetadata objectType) {
		throw new UnsupportedOperationException("This is a code stub.");
	}

	public List<String> referentialConstraintsDDL() {
		throw new UnsupportedOperationException("This is a code stub.");
		// return referentialConstraints;
	}
}
