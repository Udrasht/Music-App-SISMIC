package com.sismics.music.core.dao.dbi.dto;

import java.util.Date;

/**
 * Album DTO.
 *
 * @author jtremeaux 
 */
public class AlbumDto {
    @Override
	public String toString() {
		return "AlbumDto [id=" + id + ", userId=" + userId + ", name=" + name + ", albumArt=" + albumArt + ", artistId="
				+ artistId + ", artistName=" + artistName + ", updateDate=" + updateDate + ", userPlayCount="
				+ userPlayCount + "]";
	}

	/**
     * Album ID.
     */
    private String id;
    
    
    private String userId;
    /**
     * Album name.
     */
    private String name;
    
    /**
     * Album art ID.
     */
    private String albumArt;

    /**
     * Artist ID.
     */
    private String artistId;
    
    /**
     * Artist name.
     */
    private String artistName;
    
    /**
     * Last update date.
     */
    private Date updateDate;
    
    /**
     * User play count.
     */
    private Long userPlayCount;
    
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

    public String getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUserPlayCount() {
        return userPlayCount;
    }

    public void setUserPlayCount(Long userPlayCount) {
        this.userPlayCount = userPlayCount;
    }

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}
