/*
 * Copyright (C) 2013 Spencer Alderman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rogue.playtime.update;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Result return type for the update checker
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public enum Result {
    
    /** Successfully updated the jar file */
    UPDATED(Level.INFO, "Plugin updated for next restart!"),
    /** Update available, but not downloaded */
    UPDATE_AVAILABLE(Level.INFO, "An update is available!"),
    /** No update is available */
    NO_UPDATE(Level.INFO, "Plugin is up to date!"),
    /** Error in updater: bad plugin id provided */
    ERROR_BADID(Level.SEVERE, "Invalid plugin ID provided!"),
    /** No file found when attempting to download a new version */
    ERROR_FILENOTFOUND(Level.SEVERE, "Could not download the newest version!"),
    /** Failure to download the plugin completely */
    ERROR_DOWNLOAD_FAILED(Level.SEVERE, "Failed to download new version!"),
    /** Update check has not completed yet */
    INCOMPLETE(Level.WARNING, "Update check has not completed yet");
    
    /** The level that the message would be logged at */
    private final Level level;
    /** The message for the logger */
    private final String message;
    
    /**
     * Private constructor for {@link Result}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param level The level to log messages at
     * @param message The message to log
     */
    private Result(Level level, String message) {
        this.level = level;
        this.message = message;
    }
    
    /**
     * Logs the appropriate message
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param log The logger to log to
     */
    public void handleUpdate(Logger log) {
        log.log(this.level, this.message);
    }
    
    /**
     * The message this Result is represented by
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The message for this {@link Result}
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * The {@link Level} for this message, used in the logging process
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return The {@link Level} for this message
     */
    public Level getLogLevel() {
        return this.level;
    }
    
}