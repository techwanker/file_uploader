package com.pacificdataservices.uploadingfiles.database;

import static org.junit.Assert.assertNotNull;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatasourceServiceTest {

	@Autowired 
	private DataSource dataSource;
	
	@Test public void testOne() {
		assertNotNull(dataSource);
	}
	
}
