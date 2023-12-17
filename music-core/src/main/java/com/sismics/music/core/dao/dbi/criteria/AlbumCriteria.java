package com.sismics.music.core.dao.dbi.criteria;

/**
 * Album criteria.
 *
 * @author jtremeaux
 */
public class AlbumCriteria {
    /**
     * Album ID.
     */
    private String id;
    
    private String userId;
    /**
     * Directory ID.
     */
    private String directoryId;
    
    /**
     * Album name (like).
     */
    private String nameLike;
    
    /**
     * Artist ID.
     */
    private String artistId;

    /**
     * User ID.
     */
    
    public String getId() {
        return this.id;
    }

    public AlbumCriteria setId(String id) {
        this.id = id;
        return this;
    }
    
    public AlbumCriteria setUserId(String userId) {
        this.userId = userId;
        return this;
    }
    public String getUserId() {
        return this.userId;
    }

    public String getDirectoryId() {
        return this.directoryId;
    }

    public AlbumCriteria setDirectoryId(String directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    public String getArtistId() {
        return artistId;
    }

    public AlbumCriteria setArtistId(String artistId) {
        this.artistId = artistId;
        return this;
    }

    public String getNameLike() {
        return nameLike;
    }

    public AlbumCriteria setNameLike(String nameLike) {
        this.nameLike = nameLike;
        return this;
    }

}
