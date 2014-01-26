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
package com.rogue.playtime.listener;

import com.rogue.playtime.listener.listeners.*;
import com.rogue.playtime.Playtime;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

/**
 * Handles listeners for {@link Playtime}
 *
 * @since 2.0.0
 * @author 1Rogue
 * @version 2.0.0
 */
public class ListenerManager {
    
    private final Playtime plugin;
    private final Map<String, Listener> listeners = new HashMap<String, Listener>();
     
   /**
     * {@link ListenerManager} constructor
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param plugin The main {@link Playtime} instance
     */
    public ListenerManager(Playtime plugin) {
        
        this.plugin = plugin;
        
        this.listeners.put("example", new SomeListener());
        
        for (Listener l : this.listeners.values()) {
            this.plugin.getServer().getPluginManager().registerEvents(l, this.plugin);
        }
    }
    
    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled or not registered.
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param name Name of the listener
     * @return The listener class, null if disabled or not registered
     */
    public Listener getListener(String name) {
        return this.listeners.get(name);
    }
    
    /**
     * Returns whether or not a listener is registered under the relevant listener key
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param name The key to look for
     * @return <code>true</code> if registered, <code>false</code> otherwise
     */
    public boolean isRegistered(String name) {
        return this.listeners.containsKey(name);
    }

    /**
     * Registers a listener through bukkit and {@link ListenerManager}
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param name The name to place the listener as, cannot be the same as a current listener
     * @param listener The listener to register
     * @throws ListenerReregisterException Attempted to register a Listener under a similar key
     */
    public void registerListener(String name, Listener listener) throws ListenerReregisterException {
        if (!this.listeners.containsKey(name)) {
            this.listeners.put(name, listener);
            this.plugin.getServer().getPluginManager().registerEvents(listener, this.plugin);
        } else {
            throw new ListenerReregisterException("Listener Map already contains key: " + name);
        }
    }
    
    /**
     * Unregisters all the listeners attached to {@link Playtime}
     * 
     * @since 2.0.0
     * @version 2.0.0
     */
    public void cleanup() {
        HandlerList.unregisterAll(this.plugin);
    }
}
