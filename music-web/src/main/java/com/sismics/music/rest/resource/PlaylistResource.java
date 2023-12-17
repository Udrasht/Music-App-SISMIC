package com.sismics.music.rest.resource;

import com.sismics.music.core.dao.dbi.PlaylistDao;
import com.sismics.music.core.dao.dbi.PlaylistTrackDao;
import com.sismics.music.core.dao.dbi.TrackDao;
import com.sismics.music.core.dao.dbi.criteria.PlaylistCriteria;
import com.sismics.music.core.dao.dbi.criteria.TrackCriteria;
import com.sismics.music.core.dao.dbi.dto.PlaylistDto;
import com.sismics.music.core.dao.dbi.dto.TrackDto;
import com.sismics.music.core.model.dbi.Playlist;
import com.sismics.music.core.model.dbi.PlaylistTrack;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.util.dbi.PaginatedList;
import com.sismics.music.core.util.dbi.PaginatedLists;
import com.sismics.music.core.util.dbi.SortCriteria;
import com.sismics.music.rest.paginatedlisttemplate.CollaborativePlaylistPaginatedList;
import com.sismics.music.rest.paginatedlisttemplate.PaginatedListTemplate;
import com.sismics.music.rest.paginatedlisttemplate.PrivatePlaylistPaginatedList;
import com.sismics.music.rest.paginatedlisttemplate.PublicPlaylistPaginatedList;
import com.sismics.music.rest.playlistbuilder.CollaborativePlaylistBuilder;
import com.sismics.music.rest.playlistbuilder.PlaylistBuilder;
import com.sismics.music.rest.playlistbuilder.PlaylistDirector;
import com.sismics.music.rest.playlistbuilder.PrivatePlaylistBuilder;
import com.sismics.music.rest.playlistbuilder.PublicPlaylistBuilder;
import com.sismics.music.rest.util.JsonUtil;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.exception.ServerException;
import com.sismics.rest.util.Validation;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.List;

/**
 * Playlist REST resources.
 * 
 * @author jtremeaux
 */
@Path("/playlist")
public class PlaylistResource extends BaseResource {
	public static final String DEFAULt_playlist = "default";

	/**
	 * Create a named playlist.
	 *
	 * @param name The name
	 * @return Response
	 */
	@PUT
	public Response createPlaylist(
			@FormParam("name") String name, @FormParam("privacy") String privacy) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		Validation.required(name, "name");
		//        System.out.println("CREATING PLAYLIST PLAYLIST RESOURCE");
		//        System.out.println(principal.getId());
		//        System.out.println(principal.getName());
		//        System.out.println("playlist name "+name);
		//        System.out.println("playlist privacy "+privacy);

		// Create the playlist
		
		Playlist playlist = new Playlist();
		PlaylistDirector playlistDirector = new PlaylistDirector();
		if(privacy.equalsIgnoreCase("private")) {
			PrivatePlaylistBuilder privatePlaylistBuilder = new PrivatePlaylistBuilder();
			playlistDirector.constructPrivatePlaylist(privatePlaylistBuilder);
			playlist = privatePlaylistBuilder.getPlaylist();
		}else if(privacy.equalsIgnoreCase("public")){
			PublicPlaylistBuilder publicPlaylistBuilder = new PublicPlaylistBuilder();
			playlistDirector.constructPublicPlaylist(publicPlaylistBuilder);
			playlist = publicPlaylistBuilder.getPlaylist();
		}else if(privacy.equalsIgnoreCase("collaborative")) {
			CollaborativePlaylistBuilder collaborativePlaylistBuilder = new CollaborativePlaylistBuilder();
			playlistDirector.constructCollaborativePlaylist(collaborativePlaylistBuilder);
			playlist = collaborativePlaylistBuilder.getPlaylist();
		}
				
		
		playlist.setUserId(principal.getId());
		playlist.setName(name);
//		playlist.setPrivacy(privacy);
		Playlist.createPlaylist(playlist);

		// Output the playlist
		return renderJson(Json.createObjectBuilder()
				.add("item", Json.createObjectBuilder()
						.add("id", playlist.getId())
						.add("name", playlist.getName())
						.add("trackCount", 0)
						.add("userTrackPlayCount", 0)
						.add("privacy", playlist.getPrivacy())
						.build()));
	}

	/**
	 * Update a named playlist.
	 *
	 * @param name The name
	 * @return Response
	 */
	@POST
	@Path("{id: [a-z0-9\\-]+}")
	public Response updatePlaylist(
			@PathParam("id") String playlistId,
			@FormParam("name") String name) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		Validation.required(playlistId, "id");
		Validation.required(name, "name");

		// Get the playlist
		PlaylistDto playlistDto = new PlaylistDao().findFirstByCriteria(new PlaylistCriteria()
				.setUserId(principal.getId())
				.setDefaultPlaylist(false)
				.setId(playlistId));
		notFoundIfNull(playlistDto, "Playlist: " + playlistId);

		// Update the playlist
		Playlist playlist = new Playlist(playlistDto.getId());
		playlist.setName(name);
		Playlist.updatePlaylist(playlist);

		// Output the playlist
		return Response.ok()
				.build();
	}

	/**
	 * Inserts a track in the playlist.
	 *
	 * @param playlistId Playlist ID
	 * @param trackId Track ID
	 * @param order Insert at this order in the playlist
	 * @param clear If true, clear the playlist
	 * @return Response
	 */
	@PUT
	@Path("{id: [a-z0-9\\-]+}")
	public Response insertTrack(
			@PathParam("id") String playlistId,
			@FormParam("id") String trackId,
			@FormParam("order") Integer order,
			@FormParam("clear") Boolean clear) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		System.out.println("inserting single track");
		// Get the track
		Track track = new TrackDao().getActiveById(trackId);
		notFoundIfNull(track, "Track: " + trackId);

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria();
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		if (clear != null && clear) {
			// Delete all tracks in the playlist
			playlistTrackDao.deleteByPlaylistId(playlist.getId());
		}

		// Get the track order
		if (order == null) {
			order = playlistTrackDao.getPlaylistTrackNextOrder(playlist.getId());
		}

		// Insert the track into the playlist
		playlistTrackDao.insertPlaylistTrack(playlist.getId(), track.getId(), order);

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Inserts tracks in the playlist.
	 *
	 * @param playlistId Playlist ID
	 * @param idList List of track ID
	 * @param clear If true, clear the playlist
	 * @return Response
	 */
	@PUT
	@Path("{id: [a-z0-9\\-]+}/multiple")
	public Response insertTracks(
			@PathParam("id") String playlistId,
			@FormParam("ids") List<String> idList,
			@FormParam("clear") Boolean clear) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		System.out.println("inserting multiple tracks");
		Validation.required(idList, "ids");

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria();
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		if (clear != null && clear) {
			// Delete all tracks in the playlist
			playlistTrackDao.deleteByPlaylistId(playlist.getId());
		}

		// Get the track order
		int order = playlistTrackDao.getPlaylistTrackNextOrder(playlist.getId());

		for (String id : idList) {
			// Load the track
			TrackDao trackDao = new TrackDao();
			Track track = trackDao.getActiveById(id);
			if (track == null) {
				return Response.status(Response.Status.NOT_FOUND).build();
			}

			// Insert the track into the playlist
			playlistTrackDao.insertPlaylistTrack(playlist.getId(), track.getId(), order++);
		}

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Load a named playlist into the default playlist.
	 *
	 * @param playlistId Playlist ID
	 * @param clear If true, clear the default playlist
	 * @return Response
	 */
	@POST
	@Path("{id: [a-z0-9\\-]+}/load")
	public Response loadPlaylist(
			@PathParam("id") String playlistId,
			@FormParam("clear") Boolean clear) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		Validation.required(playlistId, "id");

		// Get the named playlist
		PlaylistDto namedPlaylist = new PlaylistDao().findFirstByCriteria(new PlaylistCriteria()
				.setUserId(principal.getId())
				.setDefaultPlaylist(false)
				.setId(playlistId));
		notFoundIfNull(namedPlaylist, "Playlist: " + playlistId);

		// Get the default playlist
		PlaylistDto defaultPlaylist = new PlaylistDao().getDefaultPlaylistByUserId(principal.getId());
		if (defaultPlaylist == null) {
			throw new ServerException("UnknownError", MessageFormat.format("Default playlist not found for user {0}", principal.getId()));
		}

		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		if (clear != null && clear) {
			// Delete all tracks in the default playlist
			playlistTrackDao.deleteByPlaylistId(defaultPlaylist.getId());
		}

		// Get the track order
		int order = playlistTrackDao.getPlaylistTrackNextOrder(namedPlaylist.getId());

		// Insert the tracks into the playlist
		List<TrackDto> trackList = new TrackDao().findByCriteria(new TrackCriteria()
				.setUserId(principal.getId())
				.setPlaylistId(namedPlaylist.getId()));
		for (TrackDto trackDto : trackList) {
			PlaylistTrack playlistTrack = new PlaylistTrack();
			playlistTrack.setPlaylistId(defaultPlaylist.getId());
			playlistTrack.setTrackId(trackDto.getId());
			playlistTrack.setOrder(order++);
			PlaylistTrack.createPlaylistTrack(playlistTrack);
		}

		// Output the playlist
		return renderJson(buildPlaylistJson(defaultPlaylist));
	}

	/**
	 * Start or continue party mode.
	 * Adds some good tracks.
	 * 
	 * @param clear If true, clear the playlist
	 * @return Response
	 */
	@POST
	@Path("party")
	public Response party(@FormParam("clear") Boolean clear) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		// Get the default playlist
		PlaylistDto playlist = new PlaylistDao().getDefaultPlaylistByUserId(principal.getId());
		if (playlist == null) {
			throw new ServerException("UnknownError", MessageFormat.format("Default playlist not found for user {0}", principal.getId()));
		}

		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		if (clear != null && clear) {
			// Delete all tracks in the playlist
			playlistTrackDao.deleteByPlaylistId(playlist.getId());
		}

		// Get the track order
		int order = playlistTrackDao.getPlaylistTrackNextOrder(playlist.getId());

		// TODO Add prefered tracks
		// Add random tracks
		PaginatedList<TrackDto> paginatedList = PaginatedLists.create();
		new TrackDao().findByCriteria(paginatedList, new TrackCriteria().setRandom(true), null, null);

		for (TrackDto trackDto : paginatedList.getResultList()) {
			// Insert the track into the playlist
			playlistTrackDao.insertPlaylistTrack(playlist.getId(), trackDto.getId(), order++);
		}

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Move the track to another position in the playlist.
	 *
	 * @param playlistId Playlist ID
	 * @param order Current track order in the playlist
	 * @param newOrder New track order in the playlist
	 * @return Response
	 */
	@POST
	@Path("{id: [a-z0-9\\-]+}/{order: [0-9]+}/move")
	public Response moveTrack(
			@PathParam("id") String playlistId,
			@PathParam("order") Integer order,
			@FormParam("neworder") Integer newOrder) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		Validation.required(order, "order");
		Validation.required(newOrder, "neworder");

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria()
				.setUserId(principal.getId());
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		// Remove the track at the current order from playlist
		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		String trackId = playlistTrackDao.removePlaylistTrack(playlist.getId(), order);
		if (trackId == null) {
			throw new ClientException("TrackNotFound", MessageFormat.format("Track not found at position {0}", order));
		}

		// Insert the track at the new order into the playlist
		playlistTrackDao.insertPlaylistTrack(playlist.getId(), trackId, newOrder);

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Remove a track from the playlist.
	 *
	 * @param playlistId Playlist ID
	 * @param order Current track order in the playlist
	 * @return Response
	 */
	@DELETE
	@Path("{id: [a-z0-9\\-]+}/{order: [0-9]+}")
	public Response delete(
			@PathParam("id") String playlistId,
			@PathParam("order") Integer order) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		Validation.required(order, "order");

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria()
				.setUserId(principal.getId());
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		// Remove the track at the current order from playlist
		PlaylistTrackDao playlistTrackDao = new PlaylistTrackDao();
		String trackId = playlistTrackDao.removePlaylistTrack(playlist.getId(), order);
		if (trackId == null) {
			throw new ClientException("TrackNotFound", MessageFormat.format("Track not found at position {0}", order));
		}

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Delete a named playlist.
	 *
	 * @param playlistId Playlist ID
	 * @return Response
	 */
	@DELETE
	@Path("{id: [a-z0-9\\-]+}")
	public Response deletePlaylist(
			@PathParam("id") String playlistId) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		// Get the playlist
		PlaylistDto playlistDto = new PlaylistDao().findFirstByCriteria(new PlaylistCriteria()
				.setDefaultPlaylist(false)
				.setUserId(principal.getId())
				.setId(playlistId));
		notFoundIfNull(playlistDto, "Playlist: " + playlistId);

		// Delete the playlist
		Playlist playlist = new Playlist(playlistDto.getId());
		Playlist.deletePlaylist(playlist);

		// Output the playlist
		return Response.ok()
				.build();
	}

	/**
	 * Returns all named playlists.
	 *
	 * @return Response
	 */
	@GET
	public Response listPlaylist(
			@QueryParam("limit") Integer limit,
			@QueryParam("offset") Integer offset,
			@QueryParam("sort_column") Integer sortColumn,
			@QueryParam("asc") Boolean asc) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}
		//        System.out.println("playlist resource list playlists");
		//        System.out.println("PRINCIPAL ID");
		//        System.out.println(principal.getId().toString());
		//        System.out.println(principal.getName().toString());


		//        System.out.println("getting private personel playlists");

		// Get the personel private playlists
		PaginatedListTemplate<PlaylistDto> privateTemplate = new PrivatePlaylistPaginatedList();
		PaginatedList<PlaylistDto> privatePlaylists = privateTemplate.getPaginatedList(limit, offset, sortColumn, asc, principal.getId());

//		        PaginatedList<PlaylistDto> privatePlaylists = PaginatedLists.create(limit, offset);
//		        SortCriteria sortCriteria = new SortCriteria(sortColumn, asc);
//		        new PlaylistDao().findByCriteria(privatePlaylists, new PlaylistCriteria()
//		        		.setDefaultPlaylist(false)
//		                .setPrivacy("private")
//		                .setUserId(principal.getId().toString()), sortCriteria, null);
		//
		////        System.out.println("private playlits fetched");
		//        
		//        // Output the list
		////        System.out.println("private playlists");
		JsonObjectBuilder response = Json.createObjectBuilder();
		JsonArrayBuilder items = Json.createArrayBuilder();
		//        for (PlaylistDto playlist : paginatedList.getResultList()) {
		for(PlaylistDto playlist : privatePlaylists.getResultList()) {
			        	System.out.println( playlist.toString());
			if(playlist.getName()==null)
				continue;
			items.add(Json.createObjectBuilder()
					.add("id", playlist.getId())
					.add("name", playlist.getName())
					.add("trackCount", playlist.getPlaylistTrackCount())
					.add("privacy", playlist.getPrivacy())
					.add("userTrackPlayCount", playlist.getUserTrackPlayCount()));
		}

		// Getting public the playlists
		//        System.out.println("getting public playlists");


		PaginatedListTemplate<PlaylistDto> publicTemplate = new PublicPlaylistPaginatedList();
		PaginatedList<PlaylistDto> publicPlaylists = publicTemplate.getPaginatedList(limit, offset, sortColumn, asc, principal.getId());

//		        PaginatedList<PlaylistDto> publicPlaylists = PaginatedLists.create(limit, offset);
//		        SortCriteria sortCriteria2 = new SortCriteria(sortColumn, asc);
//		        new PlaylistDao().findByCriteria(publicPlaylists, new PlaylistCriteria()
//		                .setPrivacy("public"), sortCriteria2, null);

		//        System.out.println("public playlists fetched");

		        System.out.println("public playlists");

		//        if the userd id is of the logged in user , then treat it as its personel playlist
		//        in collobarative plylist, any one can add, but in public playlist on the owner can add

		// Output the list
		for (PlaylistDto playlist : publicPlaylists.getResultList()) {
			System.out.println( playlist.toString());
			if(playlist.getName()==null)
				continue;
			if(playlist.getUserId() == principal.getId()) {
				items.add(Json.createObjectBuilder()
						.add("id", playlist.getId())
						.add("name", playlist.getName())
						.add("trackCount", playlist.getPlaylistTrackCount())
						.add("privacy", "private")
						.add("userTrackPlayCount", playlist.getUserTrackPlayCount()));
			}else {
				items.add(Json.createObjectBuilder()
						.add("id", playlist.getId())
						.add("name", playlist.getName())
						.add("trackCount", playlist.getPlaylistTrackCount())
						.add("privacy", playlist.getPrivacy())
						.add("userTrackPlayCount", playlist.getUserTrackPlayCount()));
			}
		}


//		        System.out.println("getting collaborative playlists");
		PaginatedListTemplate<PlaylistDto> collaborativeTemplate = new CollaborativePlaylistPaginatedList();
        PaginatedList<PlaylistDto> collaborativePlaylists = collaborativeTemplate.getPaginatedList(limit, offset, sortColumn, asc, principal.getId());
//		PaginatedList<PlaylistDto> collaborativePlaylists = PaginatedLists.create(limit, offset);
//		SortCriteria sortCriteria3 = new SortCriteria(sortColumn, asc);
//		new PlaylistDao().findByCriteria(collaborativePlaylists, new PlaylistCriteria()
//				.setPrivacy("collaborative"), sortCriteria3, null);

		//        System.out.println("collaborative playlists fetched");
		        System.out.println("collaborative playlists");
		// Output the list
		for (PlaylistDto playlist : collaborativePlaylists.getResultList()) {
			        	System.out.println( playlist.toString());
			if(playlist.getName()==null)
				continue;
			items.add(Json.createObjectBuilder()
					.add("id", playlist.getId())
					.add("name", playlist.getName())
					.add("trackCount", playlist.getPlaylistTrackCount())
					.add("privacy", playlist.getPrivacy())
					.add("userTrackPlayCount", playlist.getUserTrackPlayCount()));

		}


		response.add("total", privatePlaylists.getResultCount() + publicPlaylists.getResultCount() + collaborativePlaylists.getResultCount());
		response.add("items", items);



		return renderJson(response);
	}

	/**
	 * Returns all tracks in the playlist.
	 *
	 * @return Response
	 */
	@GET
	@Path("{id: [a-z0-9\\-]+}")
	public Response listTrack(
			@PathParam("id") String playlistId) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria();
		//                .setUserId(principal.getId());
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		criteria.setId(playlistId);
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		// Output the playlist
		return renderJson(buildPlaylistJson(playlist));
	}

	/**
	 * Removes all tracks from the playlist.
	 *
	 * @param playlistId Playlist ID
	 * @return Response
	 */
	@POST
	@Path("{id: [a-z0-9\\-]+}/clear")
	public Response clear(
			@PathParam("id") String playlistId) {
		if (!authenticate()) {
			throw new ForbiddenClientException();
		}

		// Get the playlist
		PlaylistCriteria criteria = new PlaylistCriteria()
				.setUserId(principal.getId());
		if (DEFAULt_playlist.equals(playlistId)) {
			criteria.setDefaultPlaylist(true);
		} else {
			criteria.setDefaultPlaylist(false);
			criteria.setId(playlistId);
		}
		PlaylistDto playlist = new PlaylistDao().findFirstByCriteria(criteria);
		notFoundIfNull(playlist, "Playlist: " + playlistId);

		// Delete all tracks in the playlist
		new PlaylistTrackDao().deleteByPlaylistId(playlist.getId());

		// Always return OK
		return okJson();
	}

	/**
	 * Build the JSON output of a playlist.
	 * 
	 * @param playlist Playlist
	 * @return JSON
	 */
	private JsonObjectBuilder buildPlaylistJson(PlaylistDto playlist) {
		JsonObjectBuilder response = Json.createObjectBuilder();
		JsonArrayBuilder tracks = Json.createArrayBuilder();
		TrackDao trackDao = new TrackDao();
		List<TrackDto> trackList = trackDao.findByCriteria(new TrackCriteria()
				.setUserId(principal.getId())
				.setPlaylistId(playlist.getId()));
		int i = 0;
		for (TrackDto trackDto : trackList) {
			tracks.add(Json.createObjectBuilder()
					.add("order", i++)
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

					.add("artist", Json.createObjectBuilder()
							.add("id", trackDto.getArtistId())
							.add("name", trackDto.getArtistName()))

					.add("album", Json.createObjectBuilder()
							.add("id", trackDto.getAlbumId())
							.add("name", trackDto.getAlbumName())
							.add("albumart", trackDto.getAlbumArt() != null)));
		}
		response.add("tracks", tracks);
		response.add("id", playlist.getId());
		if (playlist.getName() != null) {
			response.add("name", playlist.getName());
		}
		return response;
	}
}
