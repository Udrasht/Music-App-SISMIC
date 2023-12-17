package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.TrackChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Track unliked listener.
 *
 * @author jtremeaux
 */
public class TrackUnlikedAsyncListener implements TrackChangeAsyncListener{
    /**
     * Logger.
     */
	LoggerService<TrackUnlikedAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param trackUnlikedAsyncEvent New directory created event
     */
    @Subscribe
    public void onTrackChange(final TrackChangeAsyncEvent trackChangeAsyncEvent) throws Exception {
        
        loggerService.beforeTransactionLogs("Track unliked event: " + trackChangeAsyncEvent.toString());
        loggerService.createStopwatch();

        final User user = trackChangeAsyncEvent.getUser();
        final Track track = trackChangeAsyncEvent.getTrack();

        TransactionUtil.handle(() -> {
        if (user.getLastFmSessionToken() != null) {
            final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
            lastFmService.unloveTrack(user, track);
        }
        });
        
        loggerService.afterTransactionLogs("Track unliked completed in {0}");
    }
}
