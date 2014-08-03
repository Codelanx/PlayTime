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
package com.codelanx.playtime.listener;

import com.codelanx.playtime.Playtime;
import com.codelanx.playtime.listener.listeners.*;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.Listener;

/**
 * Manages listeners for Playtime
 *
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.5.0
 */
public class ListenerManager {
    
    private final Playtime plugin;
    private final Map<String, Listener> listeners = new HashMap();
    
    public ListenerManager(Playtime plugin) {
        
        this.plugin = plugin;
        
        if (plugin.getPlayerHandler() != null) {
            this.listeners.put("afk", new AFKListener(plugin, plugin.getConfigurationLoader().getBoolean("afk.check-chat")));
        }
        if (plugin.getConfigurationLoader().getBoolean("check.death-time")) {
            this.listeners.put("death", new DeathListener(plugin));
        }
        /*if (plugin.getEventHandler() != null) {
            this.listeners.put("event", new EventListener(plugin));
        }*/
        if (plugin.getConfigurationLoader().getBoolean("check.online-time")) {
            this.listeners.put("online", new OnlineListener(plugin));
        }
        
        for (Listener l : this.listeners.values()) {
            plugin.getServer().getPluginManager().registerEvents(l, plugin);
        }
    }
    
    /**
     * Gets a listener by its string name. Returns null if the listener is
     * disabled.
     * 
     * Available names: afk, death, event, online, update
     * 
     * @since 1.4.1
     * @version 1.4.1
     * 
     * @param name Name of the listener
     * @return The listener class, null if disabled
     */
    public Listener getListener(String name) {
        return this.listeners.get(name);
    }
    
    /**
     * Registers a Listener under Playtime
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @param name The name of the listener
     * @param l The listener to register
     */
    public void registerListener(String name, Listener l) {
        this.listeners.put(name, l);
        this.plugin.getServer().getPluginManager().registerEvents(l, this.plugin);
    }

}
