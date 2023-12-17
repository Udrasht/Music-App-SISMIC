package com.sismics.music.core.listener.async;

import com.google.common.eventbus.Subscribe;
import com.sismics.music.core.event.async.DirectoryChangeAsyncEvent;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.model.dbi.Directory;
import com.sismics.music.core.service.collection.CollectionService;
import com.sismics.music.core.util.TransactionUtil;

/**
 * Directory deleted listener.
 *
 * @author jtremeaux
 */
public class DirectoryDeletedAsyncListener implements DirectoryChangeAsyncListener {
    /**
     * Logger.
     */
	LoggerService<DirectoryDeletedAsyncListener> loggerService;

    /**
     * Process the event.
     *
     * @param directoryDeletedAsyncEvent New directory deleted event
     */
    @Subscribe
    public void onDirectoryChange(final DirectoryChangeAsyncEvent directoryChangeAsyncEvent) throws Exception {
        loggerService.beforeTransactionLogs("Directory deleted event: " + directoryChangeAsyncEvent.toString());

        final Directory directory = directoryChangeAsyncEvent.getDirectory();

        TransactionUtil.handle(() -> {
            // Stop watching the directory
            AppContext.getInstance().getCollectionWatchService().unwatchDirectory(directory);

            // Remove directory from index
            CollectionService collectionService = AppContext.getInstance().getCollectionService();
            collectionService.removeDirectoryFromIndex(directory);
        });

        loggerService.afterTransactionLogs("Collection updated in {0}ms");
    }
}
