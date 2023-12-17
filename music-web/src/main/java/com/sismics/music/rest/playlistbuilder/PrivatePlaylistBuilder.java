package com.sismics.music.rest.playlistbuilder;

import com.sismics.music.core.model.dbi.Playlist;

public class PrivatePlaylistBuilder implements PlaylistBuilder {

	private String privacy;

	@Override
	public void setPrivacy() {
		this.privacy = "private";

	}
	
	public Playlist getPlaylist() {
		return new Playlist(privacy);
	}

}
