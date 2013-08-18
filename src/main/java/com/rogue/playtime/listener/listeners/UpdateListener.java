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
package com.rogue.playtime.listener.listeners;

import com.rogue.playtime.Playtime;
import static com.rogue.playtime.Playtime.__;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Actions to take if update checking is enabled
 *
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.4.1
 */
public class UpdateListener implements Listener {

    private final Playtime plugin;
    
    public UpdateListener (Playtime p) {
        plugin = p;
    }
    
    /**
     * Sends a notification to ops/players with all of the plugin's permissions,
     * registers the player with the AFK checker if it is enabled, and fires any
     * login events registered by the player.
     *
     * @since 1.2.0
     * @versino 1.4.1
     *
     * @param e The join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("playtime.updatenotice")) {
            if (plugin.isUpdateAvailable()) {
                e.getPlayer().sendMessage(__(plugin.getCipher().getString("listener.update")));
            }
        }
    }
    
    
}
