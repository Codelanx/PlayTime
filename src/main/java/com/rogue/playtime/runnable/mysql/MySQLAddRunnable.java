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
package com.rogue.playtime.runnable.mysql;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.mysql.MySQL;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.1
 * @author 1Rogue
 * @version 1.3.0
 */
public class MySQLAddRunnable extends BukkitRunnable {

    Playtime plugin;

    public MySQLAddRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        MySQL db = new MySQL();
        boolean complete = false;
        StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`) VALUES ");
        if (players.length > 0) {
            for (Player p : players) {
                if (plugin.isAFKEnabled()) {
                    if (!plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                        sb.append("('").append(p.getName()).append("'), ");
                    }
                } else {
                    sb.append("('").append(p.getName()).append("'), ");
                }
            }
            if (sb.toString().endsWith(" VALUES ")) {
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info("No players to update.");
                }
                return;
            }
            if (plugin.getDebug() >= 1) {
                plugin.getLogger().info("Players updated!");
                if (plugin.getDebug() >= 2) {
                    plugin.getLogger().log(Level.INFO, "SQL Query for update: \n {0} ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1" + (plugin.isDeathEnabled() ? ", `deathtime`=`deathtime`+1" : "") + ", `onlinetime`=`onlinetime`+1", sb.substring(0, sb.length() - 2));
                }
            }
            complete = true;
        }
        try {
            db.open();
            if (db.checkConnection() && complete) {
                db.update(sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1" + (plugin.isDeathEnabled() ? ", `deathtime`=`deathtime`+1" : "") + ", `onlinetime`=`onlinetime`+1");
            }
            db.close();
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            if (plugin.getDebug() == 3) {
                ex.printStackTrace();
            }
        }
    }
}
