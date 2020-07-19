//package com.pacificdataservices.uploadingfiles;
//
//import java.util.List;
//
//import javax.persistence.EntityManagerFactory;
//
//import org.hibernate.HibernateException;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.transform.Transformers;
////import org.javautil.joblog.persistence.JoblogPersistenceNoOperation;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
//import org.springframework.stereotype.Service;
//
//@Service
//public class BaseDataServices {
//	private final Logger logger = LoggerFactory.getLogger(getClass());
//	
//	@Autowired
//	private EntityManagerFactory entityManagerFactory;
//
//	
////	@Autowired
////	protected HibernateJpaSessionFactoryBean sessionFactory;
//	private SessionFactory sessionFactory;
//	
//	private boolean logSteps = false;
//	
//	private String jobToken;
//	
//	//private JoblogPersistence jobLogger;
////    public static void loadSessionFactory(){
////    	 
////        Configuration configuration = new Configuration();
////        configuration.configure("/j2n-hibernate.cfg.xml");
////        configuration.addAnnotatedClass(Employee.class);
////        ServiceRegistry srvcReg = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
////        sessionFactory = configuration.buildSessionFactory(srvcReg);
////    }
//	public BaseDataServices() {
//		super();
//	}
//	public Session getSession2() {
//
//		   
//		   sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
//
//			Session session;
//			try {
//				session = sessionFactory.getCurrentSession();
//			} catch (HibernateException e) {
//				session = sessionFactory.openSession();
//			}
//			return session;
//		}
//
//	/**
//	 * Get the hibernate Session goofy, but it works.
//	 * 
//	 * @return
//	 */
////	protected Session getSession() {
////		if (sessionFactory == null) {
////			throw new IllegalStateException("sessionFactory is null");
////		}
////
////		EntityManagerFactory emf = sessionFactory.getEntityManagerFactory();
////		if (emf.unwrap(SessionFactory.class) == null) {
////			throw new NullPointerException("factory is not a hibernate factory");
////		}
////		this.hibernateFactory = emf.unwrap(SessionFactory.class);
////
////		Session session;
////		try {
////			session = hibernateFactory.getCurrentSession();
////		} catch (HibernateException e) {
////			session = hibernateFactory.openSession();
////		}
////		return session;
////	}
//
//	@Bean
//	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
//		HibernateJpaSessionFactoryBean fact = new HibernateJpaSessionFactoryBean();
//		fact.setEntityManagerFactory(emf);
//		return fact;
//	}
//
//	protected void deleteAll(String tableName, Session session) {
//		String hql = "delete from " + tableName;
//		Query query = session.createQuery(hql);
//		query.executeUpdate();
//	}
//
//	protected List getAllSql(@SuppressWarnings("rawtypes") Class clazz, String sql, Session session) {
//		logger.info("class {} sql {}", clazz.getName(), sql);
//		Query q = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(clazz));
//		List retval = q.list();
//		return (List) retval;
//
//	}
//
//	/**
//	 * Returns all rows in the specified table.
//	 * 
//	 * @param tableName the classname for the table for example APS_SRC_RULE would
//	 *                  be ApsSrcRule
//	 * @param session
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	protected List getAll(String tableName, Session session) {
//		String queryText = "from " + tableName;
//		return getList(tableName, queryText, session);
//	}
//
////	@SuppressWarnings("rawtypes")
////	protected <T> List<T> getAll(<T> Class clazz, Session session) {
////		String queryText = "from " + tableName;
////		return getList(tableName, queryText, session);
////	}
//
//	/*
//	 * Returns all rows in the specified table sorted by order by
//	 * 
//	 * Example: getAllSorted("IcItemMast","ic_item_nbr",session)
//	 * 
//	 * @param tableName the classname for the table for example APS_SRC_RULE would
//	 * be ApsSrcRule
//	 * 
//	 * @param session
//	 * 
//	 * @return
//	 */
//	@SuppressWarnings("rawtypes")
//	protected List getAllSorted(String tableName, String orderBy, Session session) {
//		String queryText = "from " + tableName + " order by " + orderBy;
//		return getList(tableName, queryText, session);
//	}
//
//	
//	@SuppressWarnings("rawtypes")
//	protected List getList(String tableName, String queryText, Session session) {
//		long start = System.nanoTime();
//
//		Query query = session.createQuery(queryText);
//		List result;
//		try {
//			result = query.list();
//		} catch (Exception re) {
//			logger.error("while processing '" + queryText + "' \n" + re.getMessage());
//			throw re;
//		}
//		long end = System.nanoTime();
//		long millis = (end - start) / 1000000;
//		logger.debug("table: " + tableName + " millis " + millis + " rows:  " + result.size());
//		return result;
//	}
//
//	protected void cleanTable(String tableName) {
//		throw new UnsupportedOperationException();
//	}
//
//	public boolean isLogSteps() {
//		return logSteps;
//	}
//
//	public void setLogSteps(boolean logSteps) {
//		this.logSteps = logSteps;
//	}
//
//	public String getJobToken() {
//		return jobToken;
//	}
//
//	public void setJobToken(String jobToken) {
//		this.jobToken = jobToken;
//	}
//
////	public JoblogPersistence getJobLogger() {
////		if (jobLogger == null) {
////			logger.warn("joblogger was null - providing no-operation logger");
////			jobLogger = new JoblogPersistenceNoOperation();
////		}
////		return jobLogger;
////	}
////
////	public void setJobLogger(JoblogPersistence jobLogger) {
////		this.jobLogger = jobLogger;
////	}
//}
