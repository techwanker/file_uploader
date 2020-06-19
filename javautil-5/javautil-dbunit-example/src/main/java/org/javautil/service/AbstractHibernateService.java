package org.javautil.service;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provides very basic calls for use with a hibernate SessionFactory. Provided
 * for extension by service classes that need to use a hibernate session.
 * 
 * @author bcm
 */
public abstract class AbstractHibernateService {

	@Autowired
	private SessionFactory sessionFactory;

	protected Session getSession() {
		return getSessionFactory().getCurrentSession();
	}

	protected Query createQuery(String queryText) {
		return getSession().createQuery(queryText);
	}

	@SuppressWarnings("unchecked")
	protected <T> T findById(Class<T> dtoClass, Object pkey) {
		Criteria criteria = getSession().createCriteria(dtoClass);
		criteria.add(Restrictions.idEq(pkey));
		return (T) criteria.uniqueResult();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
