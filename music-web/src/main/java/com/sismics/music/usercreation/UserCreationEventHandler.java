
package com.sismics.music.usercreation;

import com.sismics.music.core.event.UserCreatedEvent;
import com.sismics.music.core.model.context.AppContext;

public class UserCreationEventHandler implements UserCreationApplicationHandler {

	@Override
	public void processApplication(UserCreationApplication userCreationApplication) {

		UserCreatedEvent userCreatedEvent = new UserCreatedEvent();
		userCreatedEvent.setUser(userCreationApplication.getUser());
		AppContext.getInstance().getAsyncEventBus().post(userCreatedEvent);

	}

}
