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
package com.rogue.playtime.runnable.sqlite;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.sqlite.SQLite;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.1
 */
public class SQLiteAddRunnable extends BukkitRunnable {

    Playtime plugin;

    public SQLiteAddRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        SQLite db = new SQLite();
        if (players.length > 0) {
            StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO `playTime` ");
            StringBuilder sb2 = new StringBuilder("UPDATE `playTime` SET ");
            if (!(!plugin.isAFKEnabled() && !plugin.isDeathEnabled())) {
                if (plugin.isDeathEnabled()) {
                    sb2.append("`playtime`=`playtime`+1, `deathtime`=`deathtime`+1 WHERE username IN (");
                } else {
                    sb2.append("`playtime`=`playtime`+1 WHERE username IN (");
                }
            } else {
                sb2.append("`playtime`=`playtime`+1 WHERE username IN (");
            }
            int i = 0;
            for (Player p : players) {
                if ((plugin.isAFKEnabled()) && !plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                    sb2.append("'").append(p.getName()).append("', ");
                    if (i > 0) {
                        sb.append("UNION SELECT NULL, '").append(p.getName()).append("', 0, 0 ");
                    } else {
                        sb.append("SELECT NULL AS 'column1', '").append(p.getName()).append("' AS 'column2', 0 as 'column3', 0 AS 'column4' ");
                        i++;
                    }
                } else if (!plugin.isAFKEnabled()) {
                    sb2.append("'").append(p.getName()).append("', ");
                    if (i > 0) {
                        sb.append("UNION SELECT NULL, '").append(p.getName()).append("', 0, 0 ");
                    } else {
                        sb.append("SELECT NULL AS 'column1', '").append(p.getName()).append("' AS 'column2', 0 as 'column3', 0 AS 'column4' ");
                        i++;
                    }
                }
            }
            if (sb2.toString().endsWith(" SET ")) {
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info("No players to update.");
                }
                return;
            }
            if (plugin.getDebug() >= 2) {
                plugin.getLogger().log(Level.INFO, "SQL Query 1 for update: \n {0}", sb.substring(0, sb.length() - 1));
                plugin.getLogger().log(Level.INFO, "SQL Query 2 for update: \n {0}", sb2.substring(0, sb2.length() - 2) + ")");
            }
            try {
                db.open();

                db.update(sb.substring(0, sb.length() - 1));
                db.update(sb2.substring(0, sb2.length() - 2) + ")");

                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info("Players updated!");

                }
                db.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, null, ex);
                if (plugin.getDebug() == 3) {
                    ex.printStackTrace();
                }
                plugin.getLogger().log(Level.INFO, "SQL Query 1 for update: \n {0}", sb.substring(0, sb.length() - 1));
                plugin.getLogger().log(Level.INFO, "SQL Query 2 for update: \n {0}", sb2.substring(0, sb2.length() - 2) + ")");
            }
        }
    }
}
