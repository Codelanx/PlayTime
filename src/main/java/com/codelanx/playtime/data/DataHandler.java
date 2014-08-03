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
package com.codelanx.playtime.data;

import java.util.Map;
import java.util.UUID;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.5.0
 */
public interface DataHandler {
    
    /**
     * Gets the name for the data type in use
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return The data type name, all lowercase
     */
    public abstract String getName();
    
    /**
     * Gets a value based on name of a particular user
     * 
     * @since 1.3.0
     * @version 1.5.0
     * 
     * @param data In SQL, this would be the column. In YAML, this is the key under the pertinent user.
     * @param user The user UUID to look for
     * @return The integer value, or 0 if it is not found
     */
    public abstract int getValue(String data, UUID user);
    
    /**
     * Gets the top players within a data category (e.g. deathtime)
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param data The data type to select, used in the query
     * @param amount The amount of players to return
     * 
     * @return A map object containing the strings for the top players, and an integer value of their time
     */
    public abstract Map<String, Integer> getTopPlayers(String data, byte amount);
    
    /**
     * Gets the players within a range of a timer.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param maximum The maximum time
     * @param minimum The minimum time
     * @return ArrayList containing players within the range, empty if there are no players in range
     */
    public abstract Map<String, Integer> getPlayersInRange(String timer, int maximum, int minimum);
    
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
     * @version 1.4.0
     */
    public abstract void init();
    
    /**
     * Starts the runnable that will add data to the relevant storage location
     * at 1-minute intervals
     * 
     * @since 1.3.0
     * @version 1.4.0
     */
    public abstract void startRunnables();
    
    /**
     * Runs an asynchronous class that will get the entirety of the currently
     * used data storage method. This will in turn set a new runnable for
     * writing to the new form.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param newType The new type of data manager to use in String form
     * @param players Players to notify on conversion end
     */
    public abstract void startConversion(String newType, String... players);
    
    /**
     * Does any necessary operations before closing down, such as closing
     * sql connections, or stopping runnables.
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public abstract void cleanup();
}
