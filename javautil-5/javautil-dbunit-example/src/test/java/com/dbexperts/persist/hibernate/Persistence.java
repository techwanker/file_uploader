package com.dbexperts.persist.hibernate;



import java.io.Serializable;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Convenience factory for DAO creation.
 *
 */
public class Persistence {

	private static SessionFactory sessionFactory;
	private Logger logger = Logger.getLogger(getClass());
	
	public void setSessionFactory(SessionFactory aSessionFactory) {
		if (sessionFactory != null) {
			logger.warn("the SessionFactory was previously set");
			// todo it should be illegal to call more than once this is for Spring injection
		//	throw new IllegalStateException("the SessionFactory was previously set");
		}
		sessionFactory = aSessionFactory;
	}

	/** CT added thsi for examples
	 * 
	 * @return
	 */
	public Session getNewSession()
	{
		return sessionFactory.openSession();
	}
	
	/**
	 * We have left the abstraction layer.
	 * 
	 * Todo this should only be used by 
	 * @return
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public <T extends Serializable> Dao<T, Serializable> get(Class<T> clazz) {
		DaoImpl<T, Serializable> dao = new DaoImpl<T, Serializable>(clazz);
		dao.setSessionFactory(sessionFactory);
		return dao;
	}

	/**
	 * The specified dto is to be written to the persitence storage.
	 * 
	 * Todo discuss save or update and 
	 * @param persistentObject
	 */
	public void save(Object persistentObject) {
		getSession().save(persistentObject);
	}
	
	/**
	 * Move the session level ob jects to the persistence engine.
	 * 
	 * If the persistence engine is a relational database then 
	 * the insert, update, delete operations should be performed.
	 * 
	 * 
	 */
	public void flush() {
		getSession().flush();		
	}

	/**
	 * Move the session level objects to the persistence engine.
	 * 
	 * Remove the first level cache objects from memory;
	 * I don't know when you'd want to call this without a prior call to flush;
	 * @see #flush
	 * @see #clear()
	 * @see #flushAndClear()
	 *
	 */
	public void clear() {
		getSession().clear();		
	}
	
	public void flushAndClear() {
		flush();
		clear();
	}
	/**
	 * Sets the status of the specified persistentObject as "to be deleted".
	 * 
	 * todo show what
	 */
	public void delete(Object persistentObject) {
		getSession().delete(persistentObject);
	}
	
	
	/**
	 * Consider this and RDBMS commit.
	 * 
	 * There is no explanation I can give that is more clear than the summary definition.
	 */
	public void commit() {
		getSession().getTransaction().commit();
	}
	
	/**
	 * Begin a transaction.
	 * 
	 * You should consider whether you should be managing your transactions manually. 
	 * If this is appropriate, then this method is used to begin a transaction.
	 * 
	 * Transaction 
	 */
	
	public void beginTransaction() {
		Transaction transaction = getSession().beginTransaction();
	}
}