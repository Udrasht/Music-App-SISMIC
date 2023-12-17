package com.sismics.music.usercreation;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.sismics.music.core.constant.Constants;
import com.sismics.music.core.dao.dbi.UserDao;
import com.sismics.music.core.model.dbi.User;
import com.sismics.rest.exception.ServerException;
import com.sismics.util.LocaleUtil;

public class UserEntityCreationHandler implements UserCreationApplicationHandler {

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
		
		HttpServletRequest request = userCreationApplication.getRequest();
		
		User user = userCreationApplication.getUser();
		
		user.setRoleId(Constants.DEFAULT_USER_ROLE);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setCreateDate(new Date());

        if (localeId == null) {
            // Set the locale from the HTTP headers
            localeId = LocaleUtil.getLocaleIdFromAcceptLanguage(request.getHeader("Accept-Language"));
        }
        user.setLocaleId(localeId);
        
        // Create the user
        UserDao userDao = new UserDao();
        
        try {
        	userCreationApplication.setUserId(userDao.create(user));
        } catch (Exception e) {
            if ("AlreadyExistingUsername".equals(e.getMessage())) {
                throw new ServerException("AlreadyExistingUsername", "Login already used", e);
            } else {
                throw new ServerException("UnknownError", "Unknown Server Error", e);
            }
        }
        
        if(nextHandler != null) {
			nextHandler.processApplication(userCreationApplication);
		}
	}

}
