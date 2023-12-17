package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.LastFmUpdateChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Last.fm update loved tracks listener.
 *
 * @author jtremeaux
 */
public class LastFmUpdateLovedTrackAsyncListener implements LastFmUpdateAsyncListener{
    /**
     * Logger.
     */
	LoggerService<LastFmUpdateLovedTrackAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param lastFmUpdateLovedTrackAsyncEvent Update loved track event
     */
    @Subscribe
    public void onLastFmUpdate(final LastFmUpdateChangeAsyncEvent lastFmUpdateChangeAsyncEvent) throws Exception {
        
        loggerService.beforeTransactionLogs("Last.fm update loved track event: " + lastFmUpdateChangeAsyncEvent.toString());
        loggerService.createStopwatch();

        final User user = lastFmUpdateChangeAsyncEvent.getUser();

        TransactionUtil.handle(() -> {
            final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
            lastFmService.importLovedTrack(user);
        });

        loggerService.afterTransactionLogs("Last.fm update loved track event completed in {0}");
    }
}
