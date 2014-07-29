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
package com.codelanx.playtime.listener.listeners;

import com.codelanx.playtime.Playtime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Actions to take if the event system is enabled
 * 
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.4.1
 */
public class EventListener implements Listener {

    private final Playtime plugin;

    public EventListener(Playtime plugin) {
        this.plugin = plugin;
    }

    /**
     * Fires any login events registered by the player.
     *
     * @since 1.2.0
     * @versino 1.4.1
     *
     * @param e The join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.plugin.getEventHandler().fireLoginEvents(e.getPlayer().getName());
    }
}
