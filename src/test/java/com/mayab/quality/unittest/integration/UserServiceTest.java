/**
 * Author: Javier Caballero
 * Course: Software Quality
 * Semester: 7th
 * Description: Integration tests for UserService class, using DBUnit to set up 
 *              and verify data in the database for various user operations.
 */

package com.mayab.quality.unittest.integration;

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

class UserServiceTest extends DBTestCase {

    private UserService service;
    private UserMysqlDAO dao;
    
    // DB configuration
    public UserServiceTest() {
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://mysql:3306/calidad2024");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
        System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "123456");
    }

    @BeforeEach
    protected void setUp() throws Exception {
        dao = new UserMysqlDAO();
        service = new UserService(dao);

        IDatabaseConnection connection = getConnection(); 
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        // Ensures the database table is empty before each test, 
        // getDataSet loads initDB.xml and ensures the table is empty
        DatabaseOperation.TRUNCATE_TABLE.execute(connection, getDataSet());
        DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
        connection.close();
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
    }

    // Verifies the database table matches the expected XML DB data given for tests
    private void verifyDatabaseState(String expectedDataSetPath) throws Exception {
        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(expectedDataSetPath));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    // 1. createUser - Happy path (all data correct)
    @Test
    void addUser() throws Exception {
        service.createUser("Diego Barba", "diego.barba@example.com", "newPassword");
        verifyDatabaseState("src/resources/createUser.xml");
    }

    // 2. createUser - Try to create a user with an email already in the DB
    @Test
    void addUserDuplicateEmail() throws Exception {
        service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");
        User result = service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");
        assertThat(result, is(nullValue())); // No new user should be created
    }

    // 3. createUser - Try to create a user with short/long password
    @Test
    void addUserInvalidPassword() throws Exception {
        assertThat(service.createUser("User Short", "short@example.com", "123"), is(nullValue())); // Less than 8
        assertThat(service.createUser("User Long", "long@example.com", "a".repeat(20)), is(nullValue())); // More than 16
    }

    // 4. updateUser - Only name & password
    @Test
    void updateUser() throws Exception {
        service.createUser("Mauricio Chad", "mauricio.chad@example.com", "oldPassword");
        User updatedUser = new User("Mauricio Updated", "mauricio.chad@example.com", "newPassword");
        updatedUser.setId(1);
        service.updateUser(updatedUser);
        verifyDatabaseState("src/resources/updateUser.xml");
    }

    // 5. deleteUser
    @Test
    void deleteUser() throws Exception {
        service.createUser("Joshua Cu", "joshua.cu@example.com", "password123");
        service.deleteUser(1);
        verifyDatabaseState("src/resources/deleteUser.xml");
    }

    // 6. findUserByEmail
    @Test
    void findUserByEmail() throws Exception {
        service.createUser("Javier Caballero", "javier.caballero@example.com", "password123");
        User foundUser = service.findUserByEmail("javier.caballero@example.com");
        assertThat(foundUser, notNullValue());
        assertThat(foundUser.getEmail(), is("javier.caballero@example.com"));
    }

    // 7. findAllUsers
    @Test
    void findAllUsers() throws Exception {
        service.createUser("Diego Barba", "diego.barba@example.com", "password123");
        service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password456");

        List<User> users = service.findAllUsers();
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), is("Diego Barba"));
        assertThat(users.get(1).getName(), is("Esteban Cevera"));
    }

    // 8. findUserById
    @Test
    void findUserById() throws Exception {
        service.createUser("Diego Barba", "diego.barba@example.com", "password123");
        User user = service.findUserById(1);
        assertThat(user, notNullValue());
        assertThat(user.getId(), is(1));
    }
}
