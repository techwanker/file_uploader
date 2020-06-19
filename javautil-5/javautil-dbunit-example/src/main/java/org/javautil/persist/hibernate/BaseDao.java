package org.javautil.persist.hibernate;

import java.io.Serializable;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for DAO implementation. Supports injected session factory, shared
 * by all DAO implementations.
 * 
 * @author tim@softwareMentor.com
 */
public abstract class BaseDao<T extends Serializable, ID extends Serializable>
		implements Dao<T, ID> {

	final static Logger logger = LoggerFactory.getLogger(BaseDao.class);

	@Autowired
	private static SessionFactory sessionFactory;

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void setSessionFactory(SessionFactory aSessionFactory) {
		sessionFactory = aSessionFactory;
	}

}
