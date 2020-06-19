package org.javautil.persist.hibernate;

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

	T findById(ID id, boolean lock);

	List<T> findByExample(T entity);

	List<T> findByExample(T entity, String[] excludeProperty);

	List<T> findAll();

}
