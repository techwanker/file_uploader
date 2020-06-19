package org.javautil.persist.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;

/**
 * Common DAO implementation. Usage: 1) retrieve userDao from application
 * context, where it has session factory injected. 2) instantiate other DAOs:
 * DmDomain domainDao = new DaoImpl<DmDomain, String>(DmDomain.class);
 * 
 * @author tim@softwareMentor.com
 */
public class DaoImpl<T extends Serializable, ID extends Serializable> extends
		BaseDao<T, ID> implements Dao<T, ID> {

	protected Class<T> persistentClass;

	public DaoImpl(Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}

	public T save(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	public void delete(T entity) {
		getSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	public T findById(ID id, boolean lock) {
		T instance = (T) getSession().get(persistentClass, id);
		return instance;
	}

	public List<T> findByExample(T entity) {
		return findByExample(entity, new String[0]);

	}

	@SuppressWarnings("unchecked")
	public List<T> findByExample(T entity, String[] excludeProperty) {
		Criteria crit = getSession().createCriteria(persistentClass);
		Example example = Example.create(entity);
		for (String exclude : excludeProperty) {
			example.excludeProperty(exclude);
		}
		crit.add(example);
		return crit.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		List<T> results = getSession().createCriteria(persistentClass).list();
		return results;
	}

}
