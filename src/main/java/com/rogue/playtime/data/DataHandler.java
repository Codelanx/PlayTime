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
package com.rogue.playtime.data;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public interface DataHandler {
    
    /**
     * Gets a value based on name of a particular user
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param data In SQL, this would be the column. In YAML, this is the key under the pertinent user.
     * @param username The username to look for
     * @return The integer value, or 0 if it is not found
     */
    public abstract int getValue(String data, String username);
    
    /**
     * Calls an Asynchronous task that resets the player's death timer
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param username The username to reset
     */
    public abstract void onDeath(String username);
    
    /**
     * Calls an Asynchronous task that resets the player's online timer
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param username The username to reset
     */
    public abstract void onLogout(String username);
    
    /**
     * A void method used to double-check that the files/sql databases are
     * correctly formatted, and will not cause errors in the future.
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public abstract void verifyFormat();
    
    /**
     * Sets any necessary variables before dealing with data management.
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public abstract void setup();
    
    /**
     * Starts the runnable that will add data to the relevant storage location
     * at 1-minute intervals
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public abstract void initiateRunnable();
    
    /**
     * Does any necessary operations before closing down, such as closing
     * sql connections, or stopping runnables.
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public abstract void cleanup();
}
