package com.sismics.music.rest.resource;

import com.sismics.music.core.dao.dbi.AlbumDao;
import com.sismics.music.core.dao.dbi.ArtistDao;
import com.sismics.music.core.dao.dbi.TrackDao;
import com.sismics.music.core.dao.dbi.criteria.AlbumCriteria;
import com.sismics.music.core.dao.dbi.criteria.ArtistCriteria;
import com.sismics.music.core.dao.dbi.criteria.TrackCriteria;
import com.sismics.music.core.dao.dbi.dto.AlbumDto;
import com.sismics.music.core.dao.dbi.dto.ArtistDto;
import com.sismics.music.core.dao.dbi.dto.TrackDto;
import com.sismics.music.core.util.dbi.PaginatedList;
import com.sismics.music.core.util.dbi.PaginatedLists;
import com.sismics.music.rest.util.JsonUtil;
import com.sismics.music.thirdpartyintegration.ThirdPartyIntegrationService;
import com.sismics.music.thirdpartyintegration.ThirdPartyLastFM;
import com.sismics.music.thirdpartyintegration.ThirdPartySpotify;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.Validation;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

import java.io.IOException;

/**
 * Search REST resources.
 * 
 * @author jtremeaux
 */
@Path("/search")
public class SearchResource extends BaseResource {
    /**
     * Run a full text search.
     *
     * @param query Search query
     * @param limit Page limit
     * @param offset Page offset
     * @return Response
     */
    @GET
    @Path("{query: .+}")
    public Response get(
            @PathParam("query") String query,
            @QueryParam("limit") Integer limit,
            @QueryParam("offset") Integer offset) {

        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        Validation.required(query, "query");

        // Search tracks
        PaginatedList<TrackDto> paginatedList = PaginatedLists.create(limit, offset);
        new TrackDao().findByCriteria(paginatedList, new TrackCriteria()
                .setUserId(principal.getId())
                .setLike(query), null, null);

        JsonArrayBuilder tracks = Json.createArrayBuilder();
        JsonObjectBuilder response = Json.createObjectBuilder();
        for (TrackDto trackDto : paginatedList.getResultList()) {
            tracks.add(Json.createObjectBuilder()
                    .add("order", JsonUtil.nullable(trackDto.getOrder()))
                    .add("id", trackDto.getId())
                    .add("title", trackDto.getTitle())
                    .add("year", JsonUtil.nullable(trackDto.getYear()))
                    .add("genre", JsonUtil.nullable(trackDto.getGenre()))
                    .add("length", trackDto.getLength())
                    .add("bitrate", trackDto.getBitrate())
                    .add("vbr", trackDto.isVbr())
                    .add("format", trackDto.getFormat())
                    .add("play_count", trackDto.getUserTrackPlayCount())
                    .add("liked", trackDto.isUserTrackLike())
                    .add("album", Json.createObjectBuilder()
                            .add("id", trackDto.getAlbumId())
                            .add("name", trackDto.getAlbumName())
                            .add("albumart", trackDto.getAlbumArt() != null))
                    .add("artist", Json.createObjectBuilder()
                            .add("id", trackDto.getArtistId())
                            .add("name", trackDto.getArtistName())));
        }
        response.add("tracks", tracks);

        // Search albums
        AlbumDao albumDao = new AlbumDao();
        List<AlbumDto> albumList = albumDao.findByCriteria(new AlbumCriteria().setUserId(principal.getId()).setNameLike(query));

        JsonArrayBuilder albums = Json.createArrayBuilder();
        for (AlbumDto album : albumList) {
            albums.add(Json.createObjectBuilder()
                    .add("id", album.getId())
                    .add("name", album.getName())
                    .add("albumart", album.getAlbumArt() != null)
                    .add("artist", Json.createObjectBuilder()
                            .add("id", album.getArtistId())
                            .add("name", album.getArtistName())));
        }
        response.add("albums", albums);
        
        // Search artists
        ArtistDao artistDao = new ArtistDao();
        List<ArtistDto> artistList = artistDao.findByCriteria(new ArtistCriteria().setNameLike(query));

        JsonArrayBuilder artists = Json.createArrayBuilder();
        for (ArtistDto artist : artistList) {
            artists.add(Json.createObjectBuilder()
                    .add("id", artist.getId())
                    .add("name", artist.getName()));
        }
        response.add("artists", artists);
        
        return renderJson(response);
    }
    
    
    @GET
    @Path("/search-third-party")
    public Response searchThirdParty(@QueryParam("thirdPartyType") String thirdPartyType,
    		@QueryParam("queryType") String queryType,
    		@QueryParam("queryString") String queryString
    		) throws IOException {
    	ThirdPartyIntegrationService thirdpartyIntegrationService = new ThirdPartyIntegrationService();
    	
    	thirdpartyIntegrationService.setStrategy(new ThirdPartySpotify());
    	if(thirdPartyType.equals("SPOTIFY")) {
    		thirdpartyIntegrationService.setStrategy(new ThirdPartySpotify());
    	}else if(thirdPartyType.equals("LASTFM")) {
    		thirdpartyIntegrationService.setStrategy(new ThirdPartyLastFM());
    	}
    	
    	Response response=thirdpartyIntegrationService.searchSongs(queryString,queryType);
    	
        return response;
    	
    }
    
    @GET
    @Path("/recommend-third-party")
    public Response recommendThirdParty(@QueryParam("thirdPartyType") String thirdPartyType,
    		@QueryParam("queryType") String queryType,
    		@QueryParam("queryString") String queryString) throws IOException {
    	System.out.println(thirdPartyType);
    	System.out.println(queryType);
    	System.out.println(queryString);
    	
    	ThirdPartyIntegrationService thirdpartyIntegrationService = new ThirdPartyIntegrationService();
    	if(thirdPartyType.equals("SPOTIFY")) {
    		thirdpartyIntegrationService.setStrategy(new ThirdPartySpotify());
    	}else if(thirdPartyType.equals("LASTFM")) {
    		thirdpartyIntegrationService.setStrategy(new ThirdPartyLastFM());
    	}
    	
//    	for last fm => queryString: artists and queryType: tracks
//    	for spotify => queryString: artists and queryType : "seed_artists"
    	Response response = thirdpartyIntegrationService.recommendSongs(queryString,queryType);
    	return response;
    	
    }

}

