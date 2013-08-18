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

import com.rogue.playtime.Playtime;
import com.rogue.playtime.listener.listeners.*;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

/**
 * Manages listeners for Playtime
 *
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.4.1
 */
public class ListenerManager {
    
    private final Map<String, Listener> listeners = new HashMap();
    
    public ListenerManager(Playtime plugin) {
        
        if (plugin.getPlayerHandler() != null) {
            listeners.put("afk", new AFKListener(plugin, plugin.getConfigurationLoader().getBoolean("afk.check-chat")));
        }
        if (plugin.getConfigurationLoader().getBoolean("check.death-time")) {
            listeners.put("death", new DeathListener(plugin));
        }
        if (plugin.getEventHandler() != null) {
            listeners.put("event", new EventListener(plugin));
        }
        if (plugin.getConfigurationLoader().getBoolean("check.online-time")) {
            listeners.put("online", new OnlineListener(plugin));
        }
        if (plugin.getConfigurationLoader().getBoolean("update-check")) {
            listeners.put("update", new UpdateListener(plugin));
        }
        for (Listener l : listeners.values()) {
            Bukkit.getPluginManager().registerEvents(l, plugin);
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
        return listeners.get(name);
    }

}
