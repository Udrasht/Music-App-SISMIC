package com.sismics.music.core.event.async;

import com.google.common.base.Objects;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.model.dbi.User;


/**
 * Track change event.
 *
 * @author hkashyap0809
 */
public class TrackChangeAsyncEvent {
	/**
     * Originating user.
     */
    private User user;

    /**
     * Liked track.
     */
    private Track track;

    public TrackChangeAsyncEvent(User user, Track track) {
        this.user = user;
        this.track = track;
    }

    /**
     * Getter of user.
     *
     * @return user
     */
    public User getUser() {
        return user;
    }

    /**
     * Getter of track.
     *
     * @return track
     */
    public Track getTrack() {
        return track;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("user", user)
                .add("track", track)
                .toString();
    }
}
