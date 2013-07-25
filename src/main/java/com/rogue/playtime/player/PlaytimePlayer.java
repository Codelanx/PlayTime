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
package com.rogue.playtime.player;

import org.bukkit.Location;

/**
 *
 * @since
 * @author 1Rogue
 * @version
 */
public class PlaytimePlayer {

    private String theName = "";
    private int theTime = 0;
    private Location theSavedPlace = null;
    private boolean afk = false;

    public PlaytimePlayer(String name, int time, Location location) {
        theName = name;
        theTime = time;
        theSavedPlace = location;
    }

    /**
     * Sets the player's starting AFK time.
     *
     * @since 0.1
     * @version 0.1
     *
     * @param time The supplied time
     */
    public void setTime(int time) {
        theTime = time;
    }

    /**
     * Sets the player's location at the time of going AFK.
     *
     * @since 0.1
     * @version 0.1
     *
     * @param location The saved location
     */
    public void setSavedLocation(Location location) {
        theSavedPlace = location;
    }

    /**
     * Gets the player's starting AFK time.
     *
     * @since 0.1
     * @version 0.1
     *
     * @return Starting AFK time
     */
    public int getTime() {
        return theTime;
    }

    /**
     * Gets the player's location at the time of going AFK.
     *
     * @since 0.1
     * @version 0.1
     *
     * @return The saved location
     */
    public Location getSavedLocation() {
        return theSavedPlace;
    }

    /**
     * Sets whether or not the player is AFK.
     *
     * @since 0.1
     * @version 0.1
     *
     * @param value Value to set AFK for
     */
    public void setAFK(boolean value) {
        afk = value;
    }

    /**
     * Returns whether or not the player is AFK.
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return AFK status
     */
    public boolean isAFK() {
        return afk;
    }

    /**
     * Gets the name value for the player.
     *
     * @since 0.1
     * @version 0.1
     *
     * @return The name of the player
     */
    public String getName() {
        return theName;
    }
}