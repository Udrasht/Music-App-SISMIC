package com.sismics.music.core.listener.async;

import com.sismics.music.core.event.async.DirectoryChangeAsyncEvent;

public interface DirectoryChangeAsyncListener {
	public void onDirectoryChange(final DirectoryChangeAsyncEvent directoryChangeAsyncEvent)throws Exception;
}
