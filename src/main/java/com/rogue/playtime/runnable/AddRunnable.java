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
import com.rogue.playtime.sql.db.MySQL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since @author 1Rogue
 * @version
 */
public class AddRunnable extends BukkitRunnable {

    Playtime plugin;

    public AddRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        plugin.db = new MySQL();
        try {
            plugin.db.open();
            if (plugin.db.checkConnection() && players.length > 0) {
                StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`) VALUES ");
                for (Player p : players) {
                    sb.append("('").append(p.getName()).append("', 1), ");
                }
                plugin.db.update(sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1");
                if (plugin.getDebug() >= 1) {
                    Logger.getLogger(Playtime.class.getName()).info("Players updated!");
                    if (plugin.getDebug() >= 2) {
                        Logger.getLogger(Playtime.class.getName()).log(Level.INFO, "SQL Query for update: \n{0} ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1", sb.substring(0, sb.length() - 2));
                    }
                }
            }
            plugin.db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
            if (plugin.getDebug() == 3) {
                ex.printStackTrace();
            }
        }
    }
}
