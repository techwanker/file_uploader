package org.javautil.persist.hibernate;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javautil.logging.Events;
import org.javautil.oracle.OracleConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractor;

/**
 * Convenience factory for DAO creation.
 * 
 * @author tim@softwareMentor.com
 */
public class Persistence {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@SuppressWarnings("unchecked")
	private static Map<Class, DaoImpl> daoMap = new HashMap<Class, DaoImpl>();

	private static SessionFactory sessionFactory;
	// TOOD what does the Resource tag do?
	@Resource
	private NativeJdbcExtractor extractor;

	@SuppressWarnings("unchecked")
	public <T extends Serializable> Dao<T, Serializable> get(Class<T> clazz) {
		DaoImpl dao = daoMap.get(clazz);
		if (dao == null) {
			synchronized (this) {
				dao = new DaoImpl<T, Serializable>(clazz);
				dao.setSessionFactory(sessionFactory);
				daoMap.put(clazz, dao);
			}
		}
		return dao;
	}

	/**
	 * Provide an extracted native connection object from session connection.
	 * 
	 * @return conn
	 * @throws SQLException
	 */
	public Connection getNativeConnection() throws SQLException {
		return getNativeConnection(true);
	}

	/**
	 * Provide an extracted native connection object from session connection,
	 * and optionally begin a new transaction.
	 * 
	 * @param boolean beginTx
	 * @return conn
	 * @throws SQLException
	 */
	@SuppressWarnings("deprecation")
	public Connection getNativeConnection(boolean beginTx) throws SQLException {
		if (beginTx) {
			beginTransaction();
		}
		Connection conn = getSession().connection();
		Connection nconn = extractor.getNativeConnection(conn);
		return nconn;
	}

	public Transaction beginTransaction() {
		if (getSession().getTransaction().isActive()) {
			logger.debug("clearing session and beginning new transaction");
			getSession().clear();
		} else {
			logger.debug("beginning new transaction");
		}
		Transaction tx = getSession().beginTransaction();
		if (logger.isDebugEnabled() && Events.isRegistered("oraclesid")) {
			try {
				Connection conn = getSession().connection();
				Connection nconn = extractor.getNativeConnection(conn);
				logger.debug("oracle sid = "
						+ OracleConnectionUtil.getSid(nconn));
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return tx;
	}

	public void commit() {
		getSession().flush();
		getSession().clear();
		getSession().getTransaction().commit();
	}

	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void setSessionFactory(SessionFactory aSessionFactory) {
		sessionFactory = aSessionFactory;
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setExtractor(NativeJdbcExtractor extractor) {
		this.extractor = extractor;
	}

}
