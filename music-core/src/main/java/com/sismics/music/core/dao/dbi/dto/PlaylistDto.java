package com.sismics.music.core.dao.dbi.dto;

/**
 * Playlist DTO.
 *
 * @author jtremeaux
 */
public class PlaylistDto {
    @Override
	public String toString() {
		return "PlaylistDto [id=" + id + ", name=" + name + ", userId=" + userId + ", privacy=" + privacy
				+ ", playlistTrackCount=" + playlistTrackCount + ", userTrackPlayCount=" + userTrackPlayCount + "]";
	}

	/**
     * Playlist ID.
     */
    private String id;
    
    /**
     * Playlist name.
     */
    private String name;
    
    /**
     * Owner user ID.
     */
    private String userId;
    
    private String privacy;

    /**
     * Number of tracks in the playlist.
     */
    private Long playlistTrackCount;

    /**
     * Number of plays in the playlist.
     */
    private Long userTrackPlayCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getPlaylistTrackCount() {
        return playlistTrackCount;
    }

    public void setPlaylistTrackCount(Long playlistTrackCount) {
        this.playlistTrackCount = playlistTrackCount;
    }

    public Long getUserTrackPlayCount() {
        return userTrackPlayCount;
    }

    public void setUserTrackPlayCount(Long userTrackPlayCount) {
        this.userTrackPlayCount = userTrackPlayCount;
    }

	public String getPrivacy() {
		return privacy;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}
}
