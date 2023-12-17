package com.sismics.music.core.listener.async;

import com.sismics.music.core.event.async.PlayChangeEvent;

public interface PlayChangeAsyncListener {
	public void onPlayChange(final PlayChangeEvent playChangeEvent)throws Exception;
}
