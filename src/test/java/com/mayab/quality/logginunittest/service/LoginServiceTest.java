package com.mayab.quality.logginunittest.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mayab.quality.logginunittest.dao.IDAOUser;
import com.mayab.quality.logginunittest.model.User;
import com.mayab.quality.logginunittest.service.LoginService;

class LoginServiceTest {
	
	private IDAOUser dao;
	private User user;
	private LoginService loginService;
	
	@BeforeEach
	void setUp() throws Exception {
		 dao = mock(IDAOUser.class);
	     user = mock(User.class);
	     loginService = new LoginService(dao);
	     
	}

	@Test
	void loginSuccess_whenPasswordCorrect() {
        when(user.getPassword()).thenReturn("password123");
        when(dao.findByUsername("test@example.com")).thenReturn(user);
        
        //exercise
        boolean result = loginService.login("test@example.com", "password123");
 
        //assertion
        assertThat(result, is(true)); 
        
	}

}
