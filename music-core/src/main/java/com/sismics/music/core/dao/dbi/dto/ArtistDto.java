package com.sismics.music.core.dao.dbi.dto;

/**
 * Artist DTO.
 *
 * @author bgamard
 */
public class ArtistDto {
    /**
     * Artist ID.
     */
    private String id;
    
    /**
     * Album name.
     */
    private String name;
    private String userId;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
	public String toString() {
		return "ArtistDto [id=" + id + ", name=" + name + ", userId=" + userId + "]";
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
}
