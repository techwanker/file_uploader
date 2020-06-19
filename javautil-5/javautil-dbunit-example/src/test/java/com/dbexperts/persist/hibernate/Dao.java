package com.dbexperts.persist.hibernate;

import java.io.Serializable;
import java.util.List;

/**
 * Common interface for all DAO implementations.
 * 
 * @author tim@softwareMentor.com
 */
public interface Dao<T, ID extends Serializable> {

	T save(T entity);

	void delete(T entity);

	T findById(ID id);
	
	/**
	 * Retrieve the object by the identifier, holding a lock on the object. 
	 * 
	 * Generally the lock will be held by a database engine.
	 * 
	 * It is ok for an implemention to throw an UnsupportedOperationException if 
	 * this cannot be ensured.
	 * 
	 * 
	 * 
	 * This should not be used with "User think time".
	 * 
	 * @param id
	 * @return
	 */
	T fetchByIdWithLock(ID id);

	List<T> findByExample(T entity);

	List<T> findByExample(T entity, String[] excludeProperty);

	List<T> findAllAsList();

}
