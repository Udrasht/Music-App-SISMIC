package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.LastFmUpdateChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.User;
import com.sismics.music.core.service.lastfm.LastFmService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Last.fm registered listener.
 *
 * @author jtremeaux
 */
public class LastFmUpdateTrackPlayCountAsyncListener implements LastFmUpdateAsyncListener{
    /**
     * Logger.
     */
    LoggerService<LastFmUpdateTrackPlayCountAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param lastFmUpdateTrackPlayCountAsyncEvent Update track play count event
     */
    @Subscribe
    public void onLastFmUpdate(final LastFmUpdateChangeAsyncEvent lastFmUpdateChangeAsyncEvent) throws Exception {
    	
    	loggerService.beforeTransactionLogs("Last.fm update track play count event: " + lastFmUpdateChangeAsyncEvent.toString());
    	loggerService.createStopwatch();

        final User user = lastFmUpdateChangeAsyncEvent.getUser();

        TransactionUtil.handle(() -> {
            final LastFmService lastFmService = AppContext.getInstance().getLastFmService();
            lastFmService.importTrackPlayCount(user);
        });

        loggerService.afterTransactionLogs("Last.fm update track play count event completed in {0}");
    }
}
