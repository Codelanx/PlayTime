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
import java.sql.ResultSet;
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
public class SQLitePrintRunnable extends BukkitRunnable {

    Playtime plugin;

    public SQLitePrintRunnable(Playtime p) {
        plugin = p;
    }

    public void run() {
        Player[] players = plugin.getServer().getOnlinePlayers();
        SQLite db = new SQLite();
        try {
            db.open();
            StringBuilder sb = new StringBuilder("");
            
            ResultSet ret = db.query("SELECT * FROM `playTime`");
            boolean end = false;
            while(!end) {
                if (ret.next()) {
                    sb.append('\n').append(ret.getInt(1)).append("||").append(ret.getString(2)).append("||").append(ret.getInt(3)).append("||").append(ret.getInt(4));
                } else {
                    end = true;
                }
            }
            
            plugin.getLogger().log(Level.INFO, "Current table: {0}", sb.toString());
            
            
            db.close();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, null, ex);
            if (plugin.getDebug() == 3) {
                ex.printStackTrace();
            }
        }
    }
}
