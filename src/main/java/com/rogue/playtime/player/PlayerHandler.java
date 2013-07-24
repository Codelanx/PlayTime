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

import com.rogue.playtime.Playtime;
import java.util.HashMap;
import java.util.logging.Level;
import org.bukkit.Location;

/**
 *
 * @since 1.2
 * @author 1Rogue
 * @version 1.2
 */
public class PlayerHandler {

    private final Playtime plugin;
    private final int timer;
    private final int timeEnd;
    private HashMap<String, PlaytimePlayer> players = new HashMap<String, PlaytimePlayer>();

    public PlayerHandler(Playtime p, int interval, int timeout) {
        plugin = p;
        timer = interval;
        timeEnd = timeout;
    }

    /**
     * Updates the AFK-start time for the provided player.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The relevant player's name
     * @param time The time being AFK started
     */
    public void updatePlayer(String name, int time) {
        PlaytimePlayer temp = this.getPlayer(name);
        temp.setTime(time);
        this.putPlayer(name, temp);
    }

    /**
     * Updates the AFK-start location for the provided player.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The relevant player's name
     * @param place The place the player was at the start of being AFK
     */
    public void updatePlayer(String name, Location place) {
        PlaytimePlayer temp = this.getPlayer(name.toLowerCase());
        temp.setSavedLocation(place);
        this.putPlayer(name.toLowerCase(), temp);
    }

    /**
     * Updates the AFK-status for the provided player.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The relevant player's name
     * @param afk Whether the player is AFK or not
     */
    public void updatePlayer(String name, boolean afk) {
        PlaytimePlayer temp = this.getPlayer(name.toLowerCase());
        temp.setAFK(afk);
        this.putPlayer(name.toLowerCase(), temp);
    }

    /**
     * Adds a player to the Plugin's tracked list of players.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @param time The AFK-start time
     * @param location The location when a player went AFK
     */
    public void putPlayer(String name, int time, Location location) {
        this.putPlayer(name.toLowerCase(), new PlaytimePlayer(name.toLowerCase(), time, location));
    }

    /**
     * Adds an PlaytimePlayer to the plugin's tracked list of players.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @param player The PlaytimePlayer object of the player
     */
    public void putPlayer(String name, PlaytimePlayer player) {
        players.put(name.toLowerCase(), player);
    }

    /**
     * Removes a player from the plugin's tracked list of players
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     */
    public void remPlayer(String name) {
        players.remove(name.toLowerCase());
    }

    /**
     * Gets the plugin's instance of the player
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @return The player instance
     */
    public PlaytimePlayer getPlayer(String name) {
        return players.get(name.toLowerCase());
    }

    /**
     * Returns whether or not the player is AFK.
     *
     * @since 1.2
     * @version 1.2
     *
     * @return AFK status
     */
    public boolean isAFK(String name) {
        return players.get(name.toLowerCase()).isAFK();
    }

    /**
     * Gets the time the player went AFK. 0 if they are not AFK.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @return AFK Starting time for the player
     */
    public int checkTime(String name) {
        return this.getPlayer(name.toLowerCase()).getTime();
    }

    /**
     * Gets the location where the player went AFK. Returns null if they have
     * never gone AFK.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @return Location where a player went AFK
     */
    public Location checkLocation(String name) {
        return this.getPlayer(name.toLowerCase()).getSavedLocation();
    }

    /**
     * Sets whether or not the player is AFK.
     *
     * @since 1.2
     * @version 1.2
     *
     * @param name The player name
     * @param value Whether or not the player is AFK
     */
    public void changeAFK(String name, boolean value) {
        this.getPlayer(name.toLowerCase()).setAFK(value);
    }

    public void incrementTime(String name) {
        this.getPlayer(name.toLowerCase()).setTime(this.getPlayer(name.toLowerCase()).getTime() + timer);
        if (checkTime(name) >= timeEnd) {
            if (plugin.getDebug() >= 2) {
                plugin.getLogger().log(Level.INFO, "Setting {0} as AFK!", name);
            }
            plugin.getPlayerHandler().updatePlayer(name, true);
        }
    }

    public int getAFKTimeout() {
        return timeEnd;
    }

    public int getAFKCheckInterval() {
        return timer;
    }

    /**
     * Returns the map of all PlaytimePlayers.
     *
     * @since 1.2
     * @version 1.2
     *
     * @return Map of PlaytimePlayers players
     */
    public HashMap<String, PlaytimePlayer> getPlayers() {
        return players;
    }
}
