/**
 * Author: Javier Caballero
 * Course: Software Quality
 * Semester: 7th
 * Description: Integration tests for UserService class, using DBUnit to set up 
 *              and verify data in the database for various user operations.
 */

package com.mayab.quality.unittest.service;

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
    
    // Configuración para conexión MySQL usando el driver en Docker
    public UserServiceTest() {
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
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MySqlDataTypeFactory());
        connection.getConfig().setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        // Asegura que la tabla esté completamente vacía antes de cada prueba
        DatabaseOperation.TRUNCATE_TABLE.execute(connection, getDataSet());
        DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());
        connection.close();
    }

    // Archivo XML inicial (vacío) para limpiar la base de datos
    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream("src/resources/initDB.xml"));
    }

    // Método privado para verificar el estado de la base de datos contra el XML esperado
    private void verifyDatabaseState(String expectedDataSetPath) throws Exception {
        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(expectedDataSetPath));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        Assertion.assertEquals(expectedTable, actualTable);
    }

    // 1. Crear un usuario nuevo - Caso exitoso
    @Test
    void addUser() throws Exception {
        try {
            service.createUser("Diego Barba", "diego.barba@example.com", "newPassword");
            verifyDatabaseState("src/resources/createUser.xml");
        } catch (Exception e) {
            throw new Exception("Error en la prueba addUser: " + e.getMessage(), e);
        }
    }

    // 2. Intentar crear un usuario con un correo electrónico duplicado
    @Test
    void addUserWithDuplicateEmail() throws Exception {
        try {
            service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");
            User result = service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");
            assertThat(result, is(nullValue())); // Verifica que no se crea un usuario nuevo
        } catch (Exception e) {
            throw new Exception("Error en la prueba addUserWithDuplicateEmail: " + e.getMessage(), e);
        }
    }

    // 3. Crear un usuario con una contraseña inválida (muy corta o muy larga)
    @Test
    void addUserWithInvalidPassword() throws Exception {
        try {
            assertThat(service.createUser("User Short", "short@example.com", "123"), is(nullValue()));
            assertThat(service.createUser("User Long", "long@example.com", "a".repeat(51)), is(nullValue()));
        } catch (Exception e) {
            throw new Exception("Error en la prueba addUserWithInvalidPassword: " + e.getMessage(), e);
        }
    }

    // 4. Actualizar un usuario - Solo nombre y contraseña
    @Test
    void updateUser() throws Exception {
        try {
            service.createUser("Mauricio Chad", "mauricio.chad@example.com", "oldPassword");
            User updatedUser = new User("Mauricio Updated", "mauricio.chad@example.com", "newPassword");
            updatedUser.setId(1);
            service.updateUser(updatedUser);
            verifyDatabaseState("src/resources/updateUser.xml");
        } catch (Exception e) {
            throw new Exception("Error en la prueba updateUser: " + e.getMessage(), e);
        }
    }

    // 5. Eliminar un usuario
    @Test
    void deleteUser() throws Exception {
        try {
            service.createUser("Joshua Cu", "joshua.cu@example.com", "password123");
            service.deleteUser(1);
            verifyDatabaseState("src/resources/deleteUser.xml");
        } catch (Exception e) {
            throw new Exception("Error en la prueba deleteUser: " + e.getMessage(), e);
        }
    }

    // 6. Encontrar un usuario por correo electrónico - Caso exitoso
    @Test
    void findUserByEmail() throws Exception {
        try {
            service.createUser("Javier Caballero", "javier.caballero@example.com", "password123");
            User user = service.findUserByEmail("javier.caballero@example.com");
            assertThat(user, notNullValue());
            assertThat(user.getEmail(), is("javier.caballero@example.com"));
        } catch (Exception e) {
            throw new Exception("Error en la prueba findUserByEmail: " + e.getMessage(), e);
        }
    }

    // 7. Encontrar todos los usuarios
    @Test
    void findAllUsers() throws Exception {
        try {
            service.createUser("Diego Barba", "diego.barba@example.com", "password123");
            service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password456");

            List<User> users = service.findAllUsers();
            assertThat(users, hasSize(2));
            assertThat(users.get(0).getName(), is("Diego Barba"));
            assertThat(users.get(1).getName(), is("Esteban Cevera"));
        } catch (Exception e) {
            throw new Exception("Error en la prueba findAllUsers: " + e.getMessage(), e);
        }
    }

    // 8. Encontrar un usuario por ID - Caso exitoso
    @Test
    void findUserById() throws Exception {
        try {
            service.createUser("Diego Barba", "diego.barba@example.com", "password123");
            User user = service.findUserById(1);
            assertThat(user, notNullValue());
            assertThat(user.getId(), is(1));
        } catch (Exception e) {
            throw new Exception("Error en la prueba findUserById: " + e.getMessage(), e);
        }
    }
}
