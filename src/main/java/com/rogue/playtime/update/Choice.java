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

/**
 * Provides the updater with the correct update options based on configuration
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public enum Choice {
    
    /** Do not update at all */
    NO_UPDATE(false, false),
    /** Check for an update, but do not download */
    UPDATE_CHECK(true, false),
    /** Download an update, will happen every time if checking is disabled */
    UPDATE_JAR(false, true),
    /** Check for an update, and download if available */
    FULL_UPDATE(true, true);
    
    /** Whether or not to check for an update */
    private final boolean check;
    /** Whether or not to download a new jar */
    private final boolean download;
    
    /**
     * Private enum constructor for {@link Choice}
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param check Whether or not to check for an update
     * @param download Whether or not to download the update
     */
    private Choice(boolean check, boolean download) {
        this.check = check;
        this.download = download;
    }
    
    /**
     * Static getter for a {@link Choice} based on options
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param check Whether or not to check for an update
     * @param download Whether or not to download the update
     * @return The relevant {@link Choice}
     */
    public static Choice getChoice(boolean check, boolean download) {
        for (Choice c : Choice.values()) {
            if (c.equals(check, download)) {
                return c;
            }
        }
        return null;
    }
    
    /**
     * Compares two {@link Choice} enums
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param comp The {@link Choice} to compare to
     * @return True if same options, false otherwise
     */
    public boolean equals(Choice comp) {
        return this.check == comp.check && this.download == comp.download;
    }
    
    /**
     * Compares this {@link Choice} to two booleans based on settings
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @param check Whether or not to check for an update
     * @param download Whether or not to download the update
     * @return True if equal, false otherwise
     */
    public boolean equals(boolean check, boolean download) {
        return this.check == check && this.download == download;
    }
    
    /**
     * Returns whether or not to check for an update
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return True if checking, false otherwise
     */
    public boolean doCheck() {
        return this.check;
    }
    
    /**
     * Returns whether or not to download an update
     * 
     * @since 1.0.0
     * @version 1.0.0
     * 
     * @return True if downloading, false otherwise
     */
    public boolean doDownload() {
        return this.download;
    }
}
