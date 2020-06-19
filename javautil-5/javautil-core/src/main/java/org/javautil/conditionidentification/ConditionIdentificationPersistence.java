package org.javautil.conditionidentification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map.Entry;

import org.javautil.sql.Binds;
import org.javautil.sql.Dialect;
import org.javautil.sql.NamedSqlStatements;
import org.javautil.sql.SqlStatement;
import org.javautil.sql.SqlStatements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConditionIdentificationPersistence {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Runs a series of sql statements that return as the first column, the primary
	 * key of a table being examined an 0 or more fields used in formatting a
	 * message.
	 * 
	 * https://dzone.com/articles/java-string-format-examples
	 */

	private Connection connection;

	private SqlStatements sqlStatements;

	private Dialect dialect;
	
	private int verbosity;

	public ConditionIdentificationPersistence(Connection conn, Dialect dialect, int verbosity) {
		this.connection = conn;
		this.dialect = dialect;
		this.verbosity = verbosity;
		try {
			loadDml();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	//https://stackoverflow.com/questions/3861989/preferred-way-of-loading-resources-in-java
	private void loadDml() throws IOException {
		InputStream persistenceYamlStream = this.getClass().getResourceAsStream("UtConditionPersistenceDml.yaml");
			sqlStatements = new SqlStatements(persistenceYamlStream, connection);
			persistenceYamlStream.close();
	}

	/**
	 * 
	 * 
	 * @param binds
	 * @return
	 * @throws SQLException
	 */
	long startRun(Binds binds) throws SQLException {
		SqlStatement stmt = sqlStatements.getSqlStatement("ut_condition_run_insert");
		//logger.info("sqlStatement " + stmt);
		java.sql.Date start_ts = new java.sql.Date(System.currentTimeMillis());
		binds.put("start_ts", start_ts);
		Object generated = stmt.executeReturning(binds);
		long utConditionRunId = (Long) generated;
		//https://stackoverflow.com/questions/2647407/sql-java-h2-whats-the-best-way-to-retrieve-the-unique-id-of-the-single-item
//		long utConditionRunId;
//		if (rs.next()) {
//			utConditionRunId = rs.getLong(1);
//		} else {
//			throw new IllegalArgumentException("record not found: " + binds);
//		}
		bindsInsert(utConditionRunId, binds);
		return utConditionRunId;
	}

	void bindsInsert(long utConditionRunId, Binds binds) throws SQLException {
		SqlStatement sh = sqlStatements.getSqlStatement("ut_condition_run_parm_insert");
		sh.setConnection(connection);
	//	sh.prepare(connection, dialect);
		for (Entry<String, Object> e : binds.entrySet()) {
			Binds b = new Binds();
			b.put("UT_CONDITION_RUN_ID", utConditionRunId);
			b.put("PARM_NM", e.getKey());
			b.put("PARM_VALUE", e.getValue().toString());
			b.put("PARM_TYPE", e.getValue().getClass().getName());
			sh.executeUpdate(b);
		}

	}

	long utConditionRunStepInsert(long utConditionRunId, long beginNanos, ConditionRule rule) throws SQLException {
		SqlStatement sh = sqlStatements.getSqlStatement("ut_condition_run_step_insert");
		Binds binds = new Binds();
		long utConditionId = utConditionInsert(rule);

		binds.put("UT_CONDITION_ID", utConditionId);
		binds.put("UT_CONDITION_RUN_ID", utConditionRunId);

		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		binds.put("START_TS", now);
		long runStepId= (Long) sh.executeReturning(binds);
//		rset.next();
//		long runStepId = rset.getLong(1);
		return runStepId;

	}
	
	

	long utConditionInsert(ConditionRule rule) throws SQLException {
		SqlStatement sel = sqlStatements.getSqlStatement("ut_condition_select");
		sel.setConnection(connection);
		ResultSet rset = sel.executeQuery(rule.getBinds());
		long utConditionId;

			SqlStatement insert = sqlStatements.getSqlStatement("ut_condition_insert");
			Binds binds = rule.getBinds();
			logger.debug("Binds for condition: " + binds);
			utConditionId = (Long) insert.executeReturning(binds);
		
		return utConditionId;

	}

	void utConditionRowMsgInsert(long runStepId, long pk, String formattedString) throws SQLException {
		SqlStatement sh = sqlStatements.getSqlStatement("ut_condition_row_msg_insert");
		Binds binds = new Binds();
		binds.put("UT_CONDITION_RUN_STEP_ID", runStepId);
		binds.put("PRIMARY_KEY", pk);
		binds.put("MSG", formattedString);
		sh.executeUpdate(binds);

	}
}