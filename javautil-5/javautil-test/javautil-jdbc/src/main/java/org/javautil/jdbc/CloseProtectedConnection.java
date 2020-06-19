package org.javautil.jdbc;

import java.sql.Connection;
import java.util.concurrent.Executor;

/**
 * A WrappedConnection that cannot be closed by calling close().
 * 
 * @author bcm
 */
public class CloseProtectedConnection extends WrappedConnection {

	public CloseProtectedConnection(final Connection connection) {
		super(connection, null);
	}

	@Override
	public void close() {
		// do nothing
	}

	/*
	@Override
	//TODO why can't this be inherited?
        public int setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		throw new UnsupportedOperationException();	
                super.setNetworkTimeout(executor, milliseconds);
        }
	*/
}
