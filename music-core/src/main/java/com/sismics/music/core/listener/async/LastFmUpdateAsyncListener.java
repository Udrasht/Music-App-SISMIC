package com.sismics.music.core.listener.async;

import com.sismics.music.core.event.async.LastFmUpdateChangeAsyncEvent;

public interface LastFmUpdateAsyncListener {
	public void onLastFmUpdate(final LastFmUpdateChangeAsyncEvent lastFmUpdateChangeAsyncEvent)throws Exception;
}
