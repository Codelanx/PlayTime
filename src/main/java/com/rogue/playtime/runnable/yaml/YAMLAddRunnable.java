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
package com.rogue.playtime.runnable.yaml;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.yaml.YAML;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class YAMLAddRunnable extends BukkitRunnable {

    Playtime plugin;
    YAML yaml;

    public YAMLAddRunnable(Playtime p, YAML y) {
        plugin = p;
        yaml = y;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        for (Player p : players) {
            if (plugin.isAFKEnabled()) {
                if (!plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                    if (plugin.isDeathEnabled()) {
                        yaml.incrementValue("users." + p.getName() + ".playtime");
                        yaml.incrementValue("users." + p.getName() + ".deathtime");
                        if (plugin.getDebug() == 3) {
                            plugin.getLogger().log(Level.INFO, "Updating playtime and deathtime for {0}!", p.getName());
                        }
                    } else {
                        yaml.incrementValue("users." + p.getName() + ".playtime");
                        if (plugin.getDebug() == 3) {
                            plugin.getLogger().log(Level.INFO, "Updating playtime for {0}!");
                        }
                    }
                }
            } else {
                if (plugin.isDeathEnabled()) {
                    yaml.incrementValue("users." + p.getName() + ".playtime");
                    yaml.incrementValue("users." + p.getName() + ".deathtime");
                    if (plugin.getDebug() == 3) {
                        plugin.getLogger().log(Level.INFO, "Updating playtime and deathtime for {0}!", p.getName());
                    }
                } else {
                    yaml.incrementValue("users." + p.getName() + ".playtime");
                    if (plugin.getDebug() == 3) {
                        plugin.getLogger().log(Level.INFO, "Updating playtime for {0}!");
                    }
                }
            }
        }
        if (plugin.getDebug() >= 1) {
            plugin.getLogger().info("Players updated!");
        }
        yaml.forceSave();
    }
}