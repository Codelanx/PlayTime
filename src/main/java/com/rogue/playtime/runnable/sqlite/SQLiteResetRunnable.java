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
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class SQLiteResetRunnable extends BukkitRunnable {
    
    private final String user;
    private final String value;
    
    public SQLiteResetRunnable(String username, String column) {
        user = username;
        value = column;
    }

    public void run() {
        SQLite db = new SQLite();
        try {
            db.open();
            db.update("UPDATE `playTime` SET `" + value + "`=0 WHERE `username`='" + user + "'");
            db.close();
        } catch (SQLException ex) {
            Playtime.getPlugin().getLogger().log(Level.SEVERE, "Error updating player {0} time", value.substring(0, value.length() - 5));
            if (Playtime.getPlugin().getDebug() == 3) {
                ex.printStackTrace();
            }
        }
    }

}
