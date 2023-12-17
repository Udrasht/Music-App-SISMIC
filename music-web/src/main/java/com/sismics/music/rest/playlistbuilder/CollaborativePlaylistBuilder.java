package com.sismics.music.rest.playlistbuilder;

import com.sismics.music.core.model.dbi.Playlist;

public class CollaborativePlaylistBuilder implements PlaylistBuilder {
	private String privacy;


	@Override
	public void setPrivacy() {
		this.privacy = "collaborative";

	}
	
	public Playlist getPlaylist() {
		return new Playlist(privacy);
	}

}
