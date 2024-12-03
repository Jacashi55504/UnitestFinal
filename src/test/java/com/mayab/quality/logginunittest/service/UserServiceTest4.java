/**
 * Author: Javier Caballero
 * Course: Software Quality
 * Semester: 7th
 * Description: Integration tests for UserService class, using DBUnit to set up 
 *              and verify data in the database for various user operations.
 */

package com.mayab.quality.logginunittest.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import java.io.FileInputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mayab.quality.logginunittest.dao.UserMysqlDAO;
import com.mayab.quality.logginunittest.model.User;
import com.mayab.quality.logginunittest.service.UserService;

class UserServiceTest4 extends DBTestCase {

    private UserService service;
    private UserMysqlDAO dao;

    public UserServiceTest4() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://localhost:3307/calidad2024");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "123456");
    }

    @BeforeEach
    protected void setUp() throws Exception {
        dao = new UserMysqlDAO();
        service = new UserService(dao);

		IDatabaseConnection connection = getConnection(); 
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory()); // Error if not added
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

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
    }

    // 1. Create a new user - Happy path
    @Test
    void addUser() throws Exception {
        User user = new User("Diego Barba", "diego.barba@example.com", "newPassword");
        service.createUser(user.getName(), user.getEmail(), user.getPassword());

        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/createUser.xml"));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    // 2. Create a new user - Duplicated email
    @Test
    void addUserWithDuplicateEmail() throws Exception {
        User user = new User("Esteban Cevera", "esteban.cevera@example.com", "password123");
        service.createUser(user.getName(), user.getEmail(), user.getPassword());

        User result = service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");
        assertThat(result, is(nullValue())); // Verify no new user is created
    }

    // 3. Create a new user - Short/long password
    @Test
    void whenPasswordInvalid_saveUserTest() throws Exception {
        User shortPasswordUser = new User("User Short", "short@example.com", "123");
        User longPasswordUser = new User("User Long", "long@example.com", "a".repeat(51)); // Exceeds length limit

        assertThat(service.createUser(shortPasswordUser.getName(), shortPasswordUser.getEmail(), shortPasswordUser.getPassword()), is(nullValue()));
        assertThat(service.createUser(longPasswordUser.getName(), longPasswordUser.getEmail(), longPasswordUser.getPassword()), is(nullValue()));
    }

    // 4. Update user - Only name & password
    @Test
    void updateUser_test() throws Exception {
        User user = new User("Mauricio Chad", "mauricio.chad@example.com", "oldPassword");
        user.setId(1);
        service.createUser(user.getName(), user.getEmail(), user.getPassword());

        user.setName("Mauricio Updated");
        user.setPassword("newPassword");
        service.updateUser(user);

        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/updateUser.xml"));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    // 5. Delete user
    @Test
    void deleteUser_test() throws Exception {
        User user = new User("Joshua Cu", "joshua.cu@example.com", "password123");
        user.setId(1);
        service.createUser(user.getName(), user.getEmail(), user.getPassword());

        service.deleteUser(user.getId());

        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/deleteUser.xml"));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    // 6. Find a user by email - happy path
    @Test
    void findUserByEmail_found() throws Exception {
        User user = service.findUserByEmail("javier.caballero@example.com");

        assertThat(user, notNullValue());
        assertThat(user.getEmail(), is("javier.caballero@example.com"));
    }

    /* 6.1. Find a user by email - User is not found (Not in activity)
    @Test
    void findUserByEmail_notFound() throws Exception {
        User user = service.findUserByEmail("nonexistent@example.com");

        assertNull(user);
    }
	*/

    // 7. Find all users
    @Test
    void findAllUsers_test() throws Exception {
        List<User> users = service.findAllUsers();

        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), is("Diego Barba"));
        assertThat(users.get(1).getName(), is("Esteban Cevera"));
    }

    // 8. Find user by ID - happy path
    @Test
    void findUserById_test() throws Exception {
        User user = service.findUserById(1);

        assertThat(user, notNullValue());
        assertThat(user.getId(), is(1));
    }
}
