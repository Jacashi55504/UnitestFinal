package com.mayab.quality.integration;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileInputStream;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mayab.quality.logginunittest.dao.UserMysqlDAO;
import com.mayab.quality.logginunittest.model.User;

class UserDAOTest extends DBTestCase{
	
	UserMysqlDAO daoMysql;
	
	public UserDAOTest() {
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
	    System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://127.0.0.1:3306/calidad2024");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "123456");
	}

	@BeforeEach
	protected void setUp() throws Exception {
		// Initialize DAO
		
		daoMysql = new UserMysqlDAO(); 
		
		// Set the initial condition of the database
		
		IDatabaseConnection connection = getConnection(); 
		connection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
		try {
			DatabaseOperation.TRUNCATE_TABLE.execute(connection,getDataSet());
			DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());						
		} catch(Exception e) {
			fail("Error in setup: "+ e.getMessage()); 
		} finally {
			connection.close(); 
		}
	}

	 protected IDataSet getDataSet() throws Exception {
	        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
	 }
	 
	@Test
	public void testAddUser() {
		//Init
		User user = new User("Diego Barba", "diego.barba@example.com", "newPassword");
		
		//Exercise
		daoMysql.save(user);
			
		// Verify data in database
		try {
			// This is the full database
			IDatabaseConnection conn = getConnection();
			conn.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
			// org.dbunit.database.AmbiguousTableNameException: USUARIOS
			IDataSet databaseDataSet = conn.createDataSet(); 
			String[] tables = databaseDataSet.getTableNames();
				
			ITable actualTable = databaseDataSet.getTable("usuarios");
				
			// Read XML with the expected result
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("src/resources/createUser.xml"));
			ITable expectedTable = expectedDataSet.getTable("usuarios");
				
			Assertion.assertEquals(expectedTable, actualTable);
				
				
		} catch (Exception e) {
			// TODO: handle exception
			fail("Error in insert test: " + e.getMessage());
		}	
	}
		
	@Test
	public void testAddUserQry() {
		//Init
		User user = new User("username2", "correo2@correo.com", "123456");
		//Exercise
		int newID = daoMysql.save(user);
				
		// Verify data in database
		try {
			// This is the full database
			IDatabaseConnection conn = getConnection();
			IDataSet databaseDataSet = conn.createDataSet(); 
			
			QueryDataSet actualTable = new QueryDataSet(getConnection());
						
			actualTable.addTable("inserTMP", "SELECT * FROM usuarios WHERE id = " + newID);
						
			String actualName = (String) actualTable.getTable("inserTMP").getValue(0, "name");
			String actualEmail = (String) actualTable.getTable("inserTMP").getValue(0, "email");
			String actualPassword = (String) actualTable.getTable("inserTMP").getValue(0, "password");
						
			assertThat(actualName, is(user.getName()));
			assertThat(actualEmail, is(user.getEmail()));
			assertThat(actualPassword, is(user.getPassword()));
						
		} catch (Exception e) {
			// TODO: handle exception
			fail("Error in insert test: " + e.getMessage());
		}	
		
	}

}
