package com.sismics.util.filter;

import com.sismics.music.core.constant.Constants;
import com.sismics.music.core.model.context.AppContext;
import com.sismics.music.core.util.DirectoryUtil;
import com.sismics.music.core.util.TransactionUtil;
import com.sismics.util.EnvironmentUtil;
import com.sismics.util.context.ThreadLocalContext;
import com.sismics.util.dbi.DBIF;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.TransactionIsolationLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;

/**
 * Filter used to process a couple things in the request context.
 * 
 * @author jtremeaux
 */
public class RequestContextFilter implements Filter {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(RequestContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Force the locale in order to not depend on the execution environment
        Locale.setDefault(new Locale(Constants.DEFAULT_LOCALE_ID));

        // Initialize the app directory
        if (!filterConfig.getServletContext().getServerInfo().startsWith("Grizzly")) {
            EnvironmentUtil.setWebappContext(true);
        }
        File baseDataDirectory = null;
        try {
            baseDataDirectory = DirectoryUtil.getBaseDataDirectory();
        } catch (Exception e) {
            log.error("Error initializing base data directory", e);
        }
        if (log.isInfoEnabled()) {
            log.info(MessageFormat.format("Using base data directory: {0}", baseDataDirectory.toString()));
        }
        
        // Initialize file logger
        RollingFileAppender fileAppender = new RollingFileAppender();
        fileAppender.setName("FILE");
        fileAppender.setFile(DirectoryUtil.getLogDirectory() + File.separator + "music.log");
        fileAppender.setLayout(new PatternLayout("%d{DATE} %p %l %m %n"));
        fileAppender.setThreshold(Level.INFO);
        fileAppender.setAppend(true);
        fileAppender.setMaxFileSize("5MB");
        fileAppender.setMaxBackupIndex(5);
        fileAppender.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(fileAppender);
        
        // Initialize the application context
        TransactionUtil.handle(() -> AppContext.getInstance());
    }

    @Override
    public void destroy() {
        // NOP
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Handle handle = null;
        try {
            handle = DBIF.get().open();
        } catch (Exception e) {
            throw new ServletException("Cannot create DBI handle", e);
        }

        ThreadLocalContext context = ThreadLocalContext.get();
        context.setHandle(handle);
        handle.begin();

        // Disable transaction isolation for GET requests
        if ("GET".equals(((HttpServletRequest) request).getMethod())) {
            handle.setTransactionIsolation(TransactionIsolationLevel.READ_UNCOMMITTED);
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ThreadLocalContext.cleanup();
            
            log.error("An exception occured, rolling back current transaction", e);

            // If an unprocessed error comes up from the application layers (Jersey...), rollback the transaction
            if (handle.isInTransaction()) {
                handle.rollback();

                try {
                    handle.close();
                } catch (Exception ce) {
                    log.error("Error closing DBI handle", ce);
                }
            }
            throw new ServletException(e);
        }
        
        ThreadLocalContext.cleanup();

        // No error processing the request : commit / rollback the current transaction depending on the HTTP code
        if (handle.isInTransaction()) {
            HttpServletResponse r = (HttpServletResponse) response;
            int statusClass = r.getStatus() / 100;
            if (statusClass == 2 || statusClass == 3) {
                try {
                    handle.commit();
                } catch (Exception e) {
                    log.error("Error during commit", e);
                    r.sendError(500);
                }
            } else {
                handle.rollback();
            }

            try {
                handle.close();
            } catch (Exception e) {
                log.error("Error closing JDBI handle", e);
            }
        }
    }
}
