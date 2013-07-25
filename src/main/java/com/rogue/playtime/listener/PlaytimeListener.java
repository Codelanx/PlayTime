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
import com.rogue.playtime.runnable.yaml.MySQLDeathRunnable;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @since 1.2.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class PlaytimeListener implements Listener {
    
    private final Playtime plugin;
    
    public PlaytimeListener (Playtime p) {
        plugin = p;
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.isDeathEnabled()) {
            plugin.getDataManager().getDataHandler().onDeath(e.getEntity().getName());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin (PlayerJoinEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().putPlayer(e.getPlayer().getName(), 0, e.getPlayer().getLocation());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().remPlayer(e.getPlayer().getName());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
        }
    }
}
