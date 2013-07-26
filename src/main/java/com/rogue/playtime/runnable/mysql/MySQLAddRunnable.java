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
 * @version 1.1
 */
public class MySQLAddRunnable extends BukkitRunnable {

    Playtime plugin;

    public MySQLAddRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        MySQL db = new MySQL();
        try {
            db.open();
            if (db.checkConnection() && players.length > 0) {
                StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`, `deathtime`) VALUES ");
                for (Player p : players) {
                    if (!(!plugin.isAFKEnabled() && !plugin.isDeathEnabled()) && !plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                        if (plugin.isDeathEnabled()) {
                            sb.append("('").append(p.getName()).append("', 1, 1), ");
                        } else {
                            sb.append("('").append(p.getName()).append("', 1, 0), ");
                        }
                    } else {
                        sb.append("('").append(p.getName()).append("', 1, 0), ");
                    }
                }
                if (sb.toString().endsWith(" VALUES ")) {
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info("No players to update.");
                    }
                    db.close();
                    return;
                }
                if (plugin.isDeathEnabled()) {
                    db.update(sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1, `deathtime`=`deathtime`+1");
                } else {
                    db.update(sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1");
                }
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info("Players updated!");
                    if (plugin.getDebug() >= 2) {
                        if (plugin.isDeathEnabled()) {
                            plugin.getLogger().log(Level.INFO, "SQL Query for update: \n {0} ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1, `deathtime`=`deathtime`+1", sb.substring(0, sb.length() - 2));
                        } else {
                            plugin.getLogger().log(Level.INFO, "SQL Query for update: \n {0} ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1", sb.substring(0, sb.length() - 2));
                        }
                    }
                }
            }
            db.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            if (plugin.getDebug() == 3) {
                ex.printStackTrace();
            }
        }
    }
}
