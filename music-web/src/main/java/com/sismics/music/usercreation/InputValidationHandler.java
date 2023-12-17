package com.sismics.music.usercreation;

import com.sismics.rest.util.Validation;

public class InputValidationHandler implements UserCreationApplicationHandler{

	private UserCreationApplicationHandler nextHandler;


	public void setNextHandler(UserCreationApplicationHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	@Override
	public void processApplication(UserCreationApplication userCreationApplication) {

		String username = userCreationApplication.getUsername();
		String password = userCreationApplication.getPassword();
		String localeId = userCreationApplication.getLocaleId();
		String email = userCreationApplication.getEmail();
		
		username = Validation.length(username, "username", 3, 50);
        Validation.alphanumeric(username, "username");
        password = Validation.length(password, "password", 8, 50);
        email = Validation.length(email, "email", 3, 50);
        Validation.email(email, "email");
        
        userCreationApplication.setUsername(username);
        userCreationApplication.setPassword(password);
        userCreationApplication.setLocaleId(localeId);
        userCreationApplication.setEmail(email);

		if(nextHandler != null) {
			nextHandler.processApplication(userCreationApplication);
		}
	}

}
