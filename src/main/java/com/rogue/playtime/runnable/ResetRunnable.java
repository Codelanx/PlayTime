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
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.data.sqlite.SQLite;
import com.rogue.playtime.data.yaml.YAML;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ResetRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String user;
    private final String value;

    public ResetRunnable(Playtime p, String username, String column) {
        plugin = p;
        user = username;
        value = column;
    }

    public void run() {
        String current = plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            MySQL db = new MySQL();
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
        } else if (current.equals("sqlite")) {
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
        } else if (current.equals("flatfile")) {
            YAML yaml = new YAML();
            yaml.getFile().set("users." + user + "." + value, 0);
            yaml.forceSave();
        }
    }
}
