package org.javautil.jdbc.connectionprovider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.connection.ConnectionProvider;
import org.javautil.jdbc.H2SingletonInstance;

/**
 * 
 * @author bcm
 * 
 * @see http://www.hibernate.org/hib_docs/v3/api/org/hibernate/connection/
 * ConnectionProvider.html
 */
public class H2MemConnectionProvider implements ConnectionProvider {

	private Connection connectionInstance = null;

	public void close() throws HibernateException {
		try {
			if (connectionInstance != null && !connectionInstance.isClosed()) {
				connectionInstance.close();
			}
		} catch (SQLException e) {
			throw new HibernateException(e);
		}
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void configure(Properties arg0) throws HibernateException {
		// nothing to do here
	}

	public synchronized Connection getConnection() throws SQLException {
		if (connectionInstance == null) {
			connectionInstance = H2SingletonInstance.getConnection();
		}
		return connectionInstance;
	}

	// todo jjs what does this mean?
	public boolean supportsAggressiveRelease() {
		return false;
	}

}
