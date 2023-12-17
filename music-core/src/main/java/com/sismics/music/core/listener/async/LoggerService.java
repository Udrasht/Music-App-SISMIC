package com.sismics.music.core.listener.async;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

public class LoggerService<T> {
	
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Stopwatch stopwatch;
    
    public void beforeTransactionLogs(String message) {
    	if(log.isInfoEnabled()) {
    		log.info(message);
    	}
    	
    }
    
    public void createStopwatch() {
    	stopwatch = Stopwatch.createStarted();
    }

    public void afterTransactionLogs(String message) {
    	if(log.isInfoEnabled()) {
    		log.info(MessageFormat.format(message, stopwatch));
    	}
    	
    }
}
