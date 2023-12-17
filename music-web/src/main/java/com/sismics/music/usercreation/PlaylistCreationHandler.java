package com.sismics.music.usercreation;

import com.sismics.music.core.model.dbi.Playlist;

public class PlaylistCreationHandler implements UserCreationApplicationHandler {

	private UserCreationApplicationHandler nextHandler;
	public void setNextHandler(UserCreationApplicationHandler nextHandler) {
		this.nextHandler = nextHandler;
	}
	@Override
	public void processApplication(UserCreationApplication userCreationApplication) {

		Playlist playlist = new Playlist();
		playlist.setUserId(userCreationApplication.getUserId());
		Playlist.createPlaylist(playlist);

		if(nextHandler != null) {
			nextHandler.processApplication(userCreationApplication);
		}

	}

}
