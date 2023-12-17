package com.sismics.music.rest.playlistbuilder;

import com.sismics.music.core.model.dbi.Playlist;

public class PublicPlaylistBuilder implements PlaylistBuilder {
	private String privacy;


	@Override
	public void setPrivacy() {
		this.privacy = "public";

	}
	
	public Playlist getPlaylist() {
		return new Playlist(privacy);
	}

}
