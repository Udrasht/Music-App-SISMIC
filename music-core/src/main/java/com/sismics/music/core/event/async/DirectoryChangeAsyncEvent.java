package com.sismics.music.core.event.async;

import com.google.common.base.Objects;
import com.sismics.music.core.model.dbi.Directory;

/**
 *  Directory change event.
 *
 * @author hkashyap0809
 */
public class DirectoryChangeAsyncEvent {
	private Directory directory;
	
	public Directory getDirectory() {
		return directory;
	}
	
	public void setDirectory(Directory directory) {
		this.directory = directory;
	}
	
	@Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("directory", directory)
                .toString();
    }
}
