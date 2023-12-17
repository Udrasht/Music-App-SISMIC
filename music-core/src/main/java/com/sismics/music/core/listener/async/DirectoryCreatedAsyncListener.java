package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.DirectoryChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Directory;
import com.sismics.music.core.service.collection.CollectionService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * New directory created listener.
 *
 * @author jtremeaux
 * @param <T>
 */

public class DirectoryCreatedAsyncListener implements DirectoryChangeAsyncListener {
    /**
     * Logger.
     */
	LoggerService<DirectoryCreatedAsyncListener> loggerService ;

    /**
     * Process the event.
     *
     * @param directoryCreatedAsyncEvent New directory created event
     */
    @Subscribe
    public void onDirectoryChange(final DirectoryChangeAsyncEvent directoryChangeAsyncEvent) throws Exception {
        
        loggerService.beforeTransactionLogs("Directory created event: " + directoryChangeAsyncEvent.toString());

        final Directory directory = directoryChangeAsyncEvent.getDirectory();

        TransactionUtil.handle(() -> {
            // Index new directory
            final CollectionService collectionService = AppContext.getInstance().getCollectionService();
            collectionService.addDirectoryToIndex(directory);

            // Watch new directory
            AppContext.getInstance().getCollectionWatchService().watchDirectory(directory);

        });


        loggerService.afterTransactionLogs("Collection updated completed in {0}");
    }


}
