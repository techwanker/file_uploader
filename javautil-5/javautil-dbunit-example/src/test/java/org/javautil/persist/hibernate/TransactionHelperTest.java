package org.javautil.persist.hibernate;

import java.io.File;
import java.util.Date;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.javautil.persist.hibernate.hbm.Ticket;
import org.javautil.util.RandomUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.test.annotation.Repeat;

public class TransactionHelperTest {

	public static final String HBM_DIR = "src/test/java/org/javautil/persist/hibernate/hbm";

	private static SessionFactory sessionFactory;

	@BeforeClass
	public static void setupSesionFactory() {
		BasicConfigurator.configure();
		sessionFactory = H2InMemorySessionFactory
				.getInstance(new File(HBM_DIR));
	}

	private TransactionHelper txh;

	@Test
	public void testSessionFactoryConstructor() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		Transaction tx = txh.startTransaction();
		Assert.assertNotNull(tx);
		Assert.assertTrue(tx.isActive());
		tx.rollback();
	}

	@Test(expected = IllegalStateException.class)
	public void testSessionFactoryRequired() throws Exception {
		txh = new TransactionHelper();
		txh.afterPropertiesSet();
	}

	@Test(expected = IllegalStateException.class)
	public void testCommitWithNoTransaction() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.commit();
	}

	@Test(expected = IllegalStateException.class)
	public void testRollbackWithNoTransaction() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.rollback();
	}

	@Test(expected = IllegalStateException.class)
	public void testStartTransactionTwice() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.startTransaction();
		txh.startTransaction();
	}

	@Test(expected = IllegalStateException.class)
	public void testGetTransactionWithNoTransaction() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.getTransaction();
	}

	@Test(expected = IllegalStateException.class)
	public void testGetTransactionWithInactiveTransaction() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.startTransaction();
		txh.rollback();
		txh.getTransaction();
	}

	@Test()
	public void testStartTransaction() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		txh.startTransaction();
		txh.rollback();
		// we also make sure that we can repeat this behavior!
		txh.startTransaction();
		txh.rollback();
	}

	@Test()
	public void testIsTransactionActive() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		Assert.assertFalse(txh.isTransactionActive());
		txh.startTransaction();
		Assert.assertTrue(txh.isTransactionActive());
		txh.rollback();
		Assert.assertFalse(txh.isTransactionActive());
	}

	@Test
	public void testGetters() throws Exception {
		txh = new TransactionHelper();
		txh.setSessionFactory(sessionFactory);
		txh.afterPropertiesSet();
		Assert.assertNotNull(txh.getSessionFactory());
		txh.startTransaction();
		Assert.assertNotNull(txh.getSession());
		Assert.assertNotNull(txh.getTransaction());
		txh.rollback();
	}

	private TransactionHelper newTransactionHelper() throws Exception {
		TransactionHelper txh = new TransactionHelper(sessionFactory);
		txh.afterPropertiesSet();
		return txh;
	}

	private Ticket newTicket() {
		Ticket myTicket = new Ticket();
		myTicket.setApplicationName(getClass().getSimpleName());
		myTicket.setAuthDttm(new Date());
		myTicket.setExpireDttm(new Date());
		myTicket.setIpAddress("127.0.0.1");
		String randomHash = new RandomUtils().getRandomString('n', 32, 32);
		myTicket.setUserSessionIdHash(randomHash);
		return myTicket;
	}

	@Test
	@Repeat(value=100)
	public void testRepeatedTxCommit() throws Exception {
		if (txh == null) {
			txh = newTransactionHelper();
		}
		if (txh.isTransactionActive()) {
			txh.rollback();
		}
		txh.startTransaction();
		txh.commit();
	}
	
	@Test
	public void testSaveAndCommit() throws Exception {
		txh = newTransactionHelper();
		txh.startTransaction();
		Ticket myTicket = newTicket();
		txh.getSession().save(myTicket);
		txh.commitAndStartTransaction();
		Ticket anotherTicket = (Ticket) txh.getSession().get(Ticket.class,
				myTicket.getTicketNo());
		Assert.assertEquals(anotherTicket.getUserSessionIdHash(), myTicket
				.getUserSessionIdHash());
		txh.rollback();
	}

	@Test
	public void testSaveAndRollback() throws Exception {
		txh = newTransactionHelper();
		txh.startTransaction();
		Ticket myTicket = newTicket();
		txh.getSession().save(myTicket);
		txh.rollbackAndStartTransaction();
		Ticket anotherTicket = (Ticket) txh.getSession().get(Ticket.class,
				myTicket.getTicketNo());
		Assert.assertEquals(null, anotherTicket);
		txh.rollback();
	}

}
