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
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.2.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class SQLiteDeathRunnable extends BukkitRunnable {
    
    private final String user;
    
    public SQLiteDeathRunnable(String username) {
        user = username;
    }

    public void run() {
        SQLite db = new SQLite();
        try {
            db.open();
            db.update("UPDATE `playTime` SET `deathtime`=0 WHERE `username`='" + user + "'");
            db.close();
        } catch (SQLException ex) {
            Playtime.getPlugin().getLogger().severe("Error updating player death time");
            ex.printStackTrace();
        }
    }

}
