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

import static com.rogue.playtime.Playtime._;
import com.rogue.playtime.Playtime;
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

    public PlaytimeListener(Playtime p) {
        plugin = p;
    }
    
    /**
     * Resets a player's death timer on death.
     * 
     * @since 1.2.0
     * @version 1.2.0
     * 
     * @param e The death event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (plugin.isDeathEnabled()) {
            plugin.getDataManager().getDataHandler().onDeath(e.getEntity().getName());
        }
    }
    
    /**
     * Sends a notification to ops/players with all of the plugin's permissions.
     * Also registers the player with the AFK checker if it is enabled.
     * 
     * @since 1.2.0
     * @versino 1.3.0
     * 
     * @param e The join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().putPlayer(e.getPlayer().getName(), 0, e.getPlayer().getLocation());
        }
        if (e.getPlayer().isOp() || e.getPlayer().hasPermission("playtime.updatenotice"));
        if (plugin.getConfigurationLoader().getBoolean("update-check")) {
            if (plugin.isUpdateAvailable()) {
                e.getPlayer().sendMessage(_("[&ePlayTime&f] &6An update is available for Playtime!"));
            }
        }
    }
    
    /**
     * Resets a player's online timer and removes them from the afk check, if
     * the relevant service is enabled.
     * 
     * @since 1.2.0
     * @version 1.3.0
     * 
     * @param e The quit event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().remPlayer(e.getPlayer().getName());
        }
        if (plugin.isOnlineEnabled()) {
            plugin.getDataManager().getDataHandler().onLogout(e.getPlayer().getName());
        }
    }
    
    /**
     * Sets a player as "not AFK" when they move.
     * 
     * @since 1.2.0
     * @version 1.2.0
     * 
     * @param e The move event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
        }
    }
    
    /**
     * Sets a player as "not AFK" when they interact with something.
     * 
     * @since 1.2.0
     * @version 1.2.0
     * 
     * @param e The interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (plugin.isAFKEnabled()) {
            plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
        }
    }
}
