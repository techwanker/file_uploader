package com.dbexperts.persist.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;


/**
 * Common DAO implementation.
 * Usage:
 * 1) retrieve userDao from application context, where it has
 *    session factory injected.
 * 2) instantiate other DAOs:
 *    DmDomain domainDao = new DaoImpl<DmDomain, String>(DmDomain.class);
 * todo why extend BaseDao ??
 * @author tim@softwareMentor.com
 * @author jjs at dbexperts dot com
 * 
 * 
 */

/**
 * 
 */
public class DaoImpl<T, ID extends Serializable> 
//extends BaseDao<T, ID>
		implements Dao<T, ID> {
	private static SessionFactory sessionFactory;  // todo jjs who says that everybody wants the same session factory?
	
	protected Class<T> persistentClass;
	
	
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void setSessionFactory(SessionFactory aSessionFactory) {
		sessionFactory = aSessionFactory;
	}


	public DaoImpl(Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}

	public T save(T entity) {
		getSession().saveOrUpdate(entity);
		return entity;
	}

	/**
	 * Deletes the entity from the persistent store.
	 * 
	 * todo is a delete visible prior to flush?  
	 * todo does anybody know this does 
	 */
	public void delete(T entity) {
		getSession().delete(entity);
	}

	@SuppressWarnings("unchecked")
	// todo wtf lock isn't used
	public T findById(ID id) {
		T instance = (T) getSession().get(persistentClass, id);
		return instance;
	}

	/**
	 * Find by example excluding no properties.
	 * 
	 * todo jjs this is frail because association retrieval is not supported.
	 */
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
	public List<T> findAllAsList() {
		List<T> results = getSession().createCriteria(persistentClass).list();
		return results;
	}

	@Override
	// todo doesn't lock
	public T fetchByIdWithLock(ID id) {
		// TODO Auto-generated method stub
		return findById(id);
	}

}
