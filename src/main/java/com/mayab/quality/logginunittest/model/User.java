package com.mayab.quality.logginunittest.model;

public class User {
	private int id;
	private String name;
	private String email;
	private String password;
	private boolean isLogged;
	
	public User(String name, String email, String password) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.email = email;
		this.password = password;
		this.isLogged = false;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isLogged() {
		return isLogged;
	}
	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", isLogged="
				+ isLogged + "]";
	}

}