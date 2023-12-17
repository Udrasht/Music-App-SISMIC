package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.dao.dbi.UserDao;
import com.sismics.music.core.dao.dbi.UserTrackDao;
import com.sismics.music.core.event.async.PlayChangeEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Play completed listener.
 *
 * @author jtremeaux
 */
public class PlayCompletedAsyncListener implements PlayChangeAsyncListener{
    /**
     * Logger.
     */
    LoggerService<PlayCompletedAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param playCompletedEvent Play completed event
     */
    @Subscribe
    public void onPlayChange(final PlayChangeEvent playChangeEvent) throws Exception {
    	
    	loggerService.beforeTransactionLogs("Play completed event: " + playChangeEvent.toString());

        final String userId = playChangeEvent.getUserId();
        final Track track = playChangeEvent.getTrack();

        TransactionUtil.handle(() -> {
            // Increment the play count
            UserTrackDao userTrackDao = new UserTrackDao();
            userTrackDao.incrementPlayCount(userId, track.getId());

            final User user = new UserDao().getActiveById(userId);
            if (user != null && user.getLastFmSessionToken() != null) {
                final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
                lastFmService.scrobbleTrack(user, track);
            }
        });
    }
}
