package com.sismics.music.rest.resource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.sismics.music.core.dao.dbi.UserDao;
import com.sismics.music.core.event.async.LastFmUpdateChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.rest.exception.ClientException;
import com.sismics.rest.exception.ForbiddenClientException;
import com.sismics.rest.util.Validation;

import de.umass.lastfm.Session;

@Path("/user")
public class LastFmResource extends BaseResource{
	
	/**
     * Authenticates a user on Last.fm.
     *
     * @param lastFmUsername Last.fm username
     * @param lastFmPassword Last.fm password
     * @return Response
     */
    @PUT
    @Path("lastfm")
    public Response registerLastFm(
            @FormParam("username") String lastFmUsername,
            @FormParam("password") String lastFmPassword) {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        Validation.required(lastFmUsername, "username");
        Validation.required(lastFmPassword, "password");

        // Get the value of the session token
        final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
        Session session = lastFmService.createSession(lastFmUsername, lastFmPassword);
        // XXX We should be able to distinguish invalid user credentials from invalid api key -- update Authenticator?
        if (session == null) {
            throw new ClientException("InvalidCredentials", "The supplied Last.fm credentials is invalid");
        }

        // Store the session token (it has no expiry date)
        UserDao userDao = new UserDao();
        User user = userDao.getActiveById(principal.getId());
        user.setLastFmSessionToken(session.getKey());
        userDao.updateLastFmSessionToken(user);

        // Raise a Last.fm registered event
        AppContext.getInstance().getLastFmEventBus().post(new LastFmUpdateChangeAsyncEvent(user));
        AppContext.getInstance().getLastFmEventBus().post(new LastFmUpdateChangeAsyncEvent(user));

        // Always return ok
        JsonObject response = Json.createObjectBuilder()
                .add("status", "ok")
                .build();
        return Response.ok().entity(response).build();
    }
	
	/**
     * Returns the Last.fm information about the connected user.
     *
     * @return Response
     */
    @GET
    @Path("lastfm")
    public Response lastFmInfo() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        JsonObjectBuilder response = Json.createObjectBuilder();
        User user = new UserDao().getActiveById(principal.getId());

        if (user.getLastFmSessionToken() != null) {
            final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
            de.umass.lastfm.User lastFmUser = lastFmService.getInfo(user);
    
            response.add("username", lastFmUser.getName())
                    .add("registered_date", lastFmUser.getRegisteredDate().getTime())
                    .add("play_count", lastFmUser.getPlaycount())
                    .add("url", lastFmUser.getUrl())
                    .add("image_url", lastFmUser.getImageURL());
        } else {
            response.add("status", "not_connected");
        }

        return renderJson(response);
    }

	/**
     * Disconnect the current user from Last.fm.
     *  
     * @return Response
     */
    @DELETE
    @Path("lastfm")
    public Response unregisterLastFm() {
        if (!authenticate()) {
            throw new ForbiddenClientException();
        }

        // Remove the session token
        UserDao userDao = new UserDao();
        User user = userDao.getActiveById(principal.getId());
        user.setLastFmSessionToken(null);
        userDao.updateLastFmSessionToken(user);

        // Always return ok
        JsonObject response = Json.createObjectBuilder()
                .add("status", "ok")
                .build();
        return Response.ok().entity(response).build();
    }
}
