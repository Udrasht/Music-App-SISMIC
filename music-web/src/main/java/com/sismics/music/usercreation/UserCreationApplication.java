package com.sismics.music.usercreation;

import javax.servlet.http.HttpServletRequest;

import com.sismics.music.core.model.dbi.User;

public class UserCreationApplication {
	private boolean isRejected;
	private String username;
	private String password; 
	private String localeId;
	private String email;
	private User user;
	private String userId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	private	HttpServletRequest request;

	public UserCreationApplication(String username, String password, String localeId, String email, HttpServletRequest request) {
		isRejected = false;
		this.username = username;
		this.password = password;
		this.localeId = localeId;
		this.email = email;
		this.request=request;
		this.user = new User();
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public User getUser() {
		return user;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isRejected() {
		return isRejected;
	}

	public void setRejected(boolean isRejected) {
		this.isRejected = isRejected;
	}
}
