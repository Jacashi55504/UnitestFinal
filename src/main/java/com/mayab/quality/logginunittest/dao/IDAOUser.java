package com.mayab.quality.logginunittest.dao;

import java.util.List;

import com.mayab.quality.logginunittest.model.User;

public interface IDAOUser {
	User findByUsername(String name);

	int save(User user);

	User findUserByEmail(String email);

	List<User> findAll();

	User findById(int id);

	boolean deleteById(int id);

	User updateUser(User userOld);
}
