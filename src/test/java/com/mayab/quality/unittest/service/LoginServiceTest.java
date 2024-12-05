/**
 * Author: Javier Caballero
 * Course: Software Quality
 * Semester: 7th
 * Description: Unit tests for UserService class, using Mockito to mock 
 *              dependencies and JUnit for testing various user operations, 
 *              like CRUD functionalities.
 */

package com.mayab.quality.unittest.service;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mayab.quality.logginunittest.dao.IDAOUser;
import com.mayab.quality.logginunittest.model.User;
import com.mayab.quality.logginunittest.service.UserService;

class LoginServiceTest {

	private UserService service;
	private IDAOUser dao;
	private HashMap<Integer, User> db;

	@BeforeEach
	public void setUp() {
		dao = mock(IDAOUser.class); 
		service = new UserService(dao); 
		db = new HashMap<>();  
	}

	// 1. Create a new user - Happy path
	@Test
	void whenAllDataCorrect_saveUserTest() {
		// Mock the findUserByEmail and save methods for successful creation
		when(dao.findUserByEmail(anyString())).thenReturn(null);
		when(dao.save(any(User.class))).thenAnswer((Answer<Integer>) invocation -> {
			User user = invocation.getArgument(0);
			int id = db.size() + 1;  // Simulate auto-increment ID
			db.put(id, user);
			return id;
		});

		User savedUser = service.createUser("Diego Barba", "diego.barba@example.com", "newPassword");

		// Verify the user was created with correct details
		assertThat(savedUser, notNullValue());
		assertThat(savedUser.getName(), is("Diego Barba"));
		assertThat(savedUser.getEmail(), is("diego.barba@example.com"));
		assertThat(savedUser.getPassword(), is("newPassword"));
	}

	// 2. Create a new user - Duplicated email
	@Test
	void whenEmailDuplicate_saveUserTest() {
		// Set up existing user to trigger duplicate email scenario
		User existingUser = new User("Esteban Cevera", "esteban.cevera@example.com", "password123");
		when(dao.findUserByEmail(existingUser.getEmail())).thenReturn(existingUser);

		User savedUser = service.createUser("Esteban Cevera", "esteban.cevera@example.com", "password123");

		// Verify no new user is created (returns existing user)
		assertThat(savedUser, is(existingUser));
	}

	// 3. Update user - Update password
	@Test
	void updatePassword_test() {
		// Setup old user and updated user data
		User oldUser = new User("Mauricio Chad", "mauricio.chad@example.com", "oldPassword");
		oldUser.setId(1);
		db.put(1, oldUser);
		
		User updatedUser = new User("Mauricio Chad", "mauricio.chad@example.com", "newPassword");
		updatedUser.setId(1);
		
		when(dao.findById(1)).thenReturn(oldUser);  // Mock finding the user by ID
		when(dao.updateUser(any(User.class))).thenAnswer((Answer<User>) invocation -> {
			User arg = invocation.getArgument(0);
			db.replace(arg.getId(), arg);  // Update in in-memory DB
			return db.get(arg.getId());
		});
		
		User result = service.updateUser(updatedUser);
		
		// Check that password is updated
		assertThat(result.getPassword(), is("newPassword"));
	}
	
	// 4. Delete user
	@Test
	void deleteUser_test() {
		User user = new User("Joshua Cu", "joshua.cu@example.com", "password123");
		user.setId(1);
		db.put(user.getId(), user);  // Add user to in-memory DB

		// Mock deletion by returning true if user exists in db
		when(dao.deleteById(1)).thenAnswer((Answer<Boolean>) invocation -> db.remove(invocation.getArgument(0)) != null);

		boolean result = service.deleteUser(1);

		// Verify deletion result and confirm user is removed from db
		assertThat(result, is(true));
		assertNull(db.get(1));
	}

	// 5. Find a user by email - happy path
	@Test
	void findUserByEmail_found() {
		User user = new User("Javier Caballero", "javier.caballero@example.com", "password123");
		when(dao.findUserByEmail("javier.caballero@example.com")).thenReturn(user);  // Mock found user

		User foundUser = service.findUserByEmail("javier.caballero@example.com");

		// Verify found user details
		assertThat(foundUser, notNullValue());
		assertThat(foundUser.getEmail(), is("javier.caballero@example.com"));
	}

	// 6. Find a user by email - User is not found
	@Test
	void findUserByEmail_notFound() {
		when(dao.findUserByEmail("nonexistent@example.com")).thenReturn(null);  // Mock no user found

		User foundUser = service.findUserByEmail("nonexistent@example.com");

		// Verify null result when user not found
		assertThat(foundUser, is(nullValue()));
	}

	// 7. Find all users
	@Test
	void findAllUsers_test() {
		// Add sample users to in-memory database
		User user1 = new User("Diego Barba", "diego.barba@example.com", "password123");
		User user2 = new User("Esteban Cevera", "esteban.cevera@example.com", "password456");

		db.put(1, user1);
		db.put(2, user2);

		when(dao.findAll()).thenReturn(new ArrayList<>(db.values()));  // Mock return of all users

		List<User> result = service.findAllUsers();

		// Verify all users are returned
		assertThat(result, hasSize(2));
		assertThat(result.get(0).getName(), is("Diego Barba"));
		assertThat(result.get(1).getName(), is("Esteban Cevera"));
	}
}
