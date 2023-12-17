package com.sismics.music.core.util;

import java.io.File;

import org.apache.commons.lang.StringUtils;

import com.sismics.util.EnvironmentUtil;

/**
 * Utilities to gain access to the storage directories used by the application.
 * 
 * @author jtremeaux
 */
public class DirectoryUtil {
    /**
     * Returns the base data directory.
     * 
     * @return Base data directory
     */
    public static File getBaseDataDirectory() {
        File baseDataDir = null;
        if (StringUtils.isNotBlank(EnvironmentUtil.getMusicHome())) {
            // If the music.home property is set then use it
            baseDataDir = new File(EnvironmentUtil.getMusicHome());
        }/* else if (false) {
            // XXX inject a variable from context.xml or similar (#41)
        }*/ else if (EnvironmentUtil.isUnitTest()) {
            // For unit testing, use a temporary directory
            baseDataDir = new File(System.getProperty("java.io.tmpdir"));
        } else {
            // We are in a webapp environment and nothing is specified, use the default directory for this OS
            if (EnvironmentUtil.isUnix()) {
                baseDataDir = new File("/var/music");
            } if (EnvironmentUtil.isWindows()) {
                baseDataDir = new File(EnvironmentUtil.getWindowsAppData() + "\\Sismics\\Music");
            } else if (EnvironmentUtil.isMacOs()) {
                baseDataDir = new File(EnvironmentUtil.getMacOsUserHome() + "/Library/Sismics/Music");
            }
        }

        if (baseDataDir != null && !baseDataDir.isDirectory()) {
            baseDataDir.mkdirs();
        }

        return baseDataDir;
    }
    
    /**
     * Returns the database directory.
     * 
     * @return Database directory.
     */
    public static File getDbDirectory() {
        return getDataSubDirectory("db");
    }

    /**
     * Returns the log directory.
     * 
     * @return Log directory.
     */
    public static File getLogDirectory() {
        return getDataSubDirectory("log");
    }
    
    /**
     * Return the album art directory.
     * 
     * @return Album art directory.
     */
    public static File getAlbumArtDirectory() {
        return getDataSubDirectory("albumart");
    }
    
    /**
     * Return the import audio directory.
     * 
     * @return Import audio directory.
     */
    public static File getImportAudioDirectory() {
        return getDataSubDirectory("importaudio");
    }

    /**
     * Returns a subdirectory of the base data directory
     * 
     * @return Subdirectory
     */
    private static File getDataSubDirectory(String subdirectory) {
        File baseDataDir = getBaseDataDirectory();
        File dataSubDirectory = new File(baseDataDir.getPath() + File.separator + subdirectory);
        if (!dataSubDirectory.isDirectory()) {
            dataSubDirectory.mkdirs();
        }
        return dataSubDirectory;
    }
}
