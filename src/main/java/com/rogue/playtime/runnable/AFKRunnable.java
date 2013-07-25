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
package com.rogue.playtime.runnable;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.player.PlaytimePlayer;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.2.0
 * @author 1Rogue
 * @version 1.2.0
 */
public class AFKRunnable extends BukkitRunnable {
    
    Playtime plugin;

    public AFKRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Map<String, PlaytimePlayer> players = plugin.getPlayerHandler().getPlayers();
        if (plugin.getDebug() >= 1) {
            plugin.getLogger().info("AFK check initiated");
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!players.get(p.getName().toLowerCase()).isAFK() && p.getLocation().equals(players.get(p.getName().toLowerCase()).getSavedLocation())) {
                plugin.getPlayerHandler().incrementTime(p.getName());
                if (plugin.getDebug() >= 3) {
                    plugin.getLogger().log(Level.INFO, "Time incremented for {0}!", p.getName());
                }
            } else if (!players.get(p.getName().toLowerCase()).isAFK()) {
                plugin.getPlayerHandler().updatePlayer(p.getName(), p.getLocation());
                if (plugin.getDebug() >= 2) {
                    plugin.getLogger().log(Level.INFO, "Updating location for {0}!", p.getName());
                }
            }
        }
    }
    
}
