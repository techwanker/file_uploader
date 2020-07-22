package com.pacificdataservices.uploadingfiles.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pacificdataservices.user.UserTable;
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasourceServiceTest {

	private transient Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired 
	private DataSource dataSource;

	@Test public void testOne() {
		assertNotNull(dataSource);
	}

	@Test public void testTwo() throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery("select 'x'");
		rset.next();
		assertEquals("x",rset.getString(1));
		conn.close();
		logger.info("it worked");
	}

	@Test public void testConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		Statement ss = conn.createStatement();
		ss.execute("drop table user if exists");
		UserTable userTable = new UserTable();
		ss.execute(UserTable.createTable);
		
	

		PreparedStatement ps  = conn.prepareStatement(UserTable.insert);
		ps.setInt(1,0);
		ps.setString(2,"admin");
		ps.execute();
		//		
		//		rset.close();
		conn.commit();
		logger.info("inserted user");

	}
}
