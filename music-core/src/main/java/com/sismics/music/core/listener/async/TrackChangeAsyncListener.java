package com.sismics.music.core.listener.async;

import com.sismics.music.core.event.async.TrackChangeAsyncEvent;

public interface TrackChangeAsyncListener {
	public void onTrackChange(final TrackChangeAsyncEvent trackChangeAsyncEvent)throws Exception;
}
