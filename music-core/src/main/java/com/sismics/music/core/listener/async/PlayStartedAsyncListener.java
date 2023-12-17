package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.dao.dbi.UserDao;
import com.sismics.music.core.event.async.PlayChangeEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Track;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Play started listener.
 *
 * @author jtremeaux
 */
public class PlayStartedAsyncListener implements PlayChangeAsyncListener {
    /**
     * Logger.
     */
	LoggerService<PlayStartedAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param playStartedEvent Play started event
     */
    @Subscribe
    public void onPlayChange(final PlayChangeEvent playChangeEvent) throws Exception {    	
    	loggerService.beforeTransactionLogs("Play started event: " + playChangeEvent.toString());

        final String userId = playChangeEvent.getUserId();
        final Track track = playChangeEvent.getTrack();

        TransactionUtil.handle(() -> {
            final User user = new UserDao().getActiveById(userId);
            if (user != null && user.getLastFmSessionToken() != null) {
                final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
                lastFmService.nowPlayingTrack(user, track);
            }
        });
    }
}
