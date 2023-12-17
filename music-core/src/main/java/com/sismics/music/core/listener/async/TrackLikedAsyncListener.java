package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.TrackChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Track liked listener.
 *
 * @author jtremeaux
 */
public class TrackLikedAsyncListener implements TrackChangeAsyncListener {
    /**
     * Logger.
     */
    LoggerService<TrackLikedAsyncListener> loggerService;
    /**
     * Process the event.
     *
     * @param trackLikedAsyncEvent New directory created event
     */
    
    @Subscribe
    public void onTrackChange(final TrackChangeAsyncEvent trackChangeAsyncEvent) throws Exception {
        
        loggerService.beforeTransactionLogs("Track liked event: " + trackChangeAsyncEvent.toString());
        loggerService.createStopwatch();

        final User user = trackChangeAsyncEvent.getUser();
        final Track track = trackChangeAsyncEvent.getTrack();

        TransactionUtil.handle(() -> {
        if (user.getLastFmSessionToken() != null) {
            final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
            lastFmService.loveTrack(user, track);
        }
        });

        loggerService.afterTransactionLogs("Track liked completed in {0}");
    }
}
