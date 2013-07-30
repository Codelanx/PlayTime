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
import com.rogue.playtime.callable.SendMessageCallable;
import com.rogue.playtime.data.DataManager;
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.data.sqlite.SQLite;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ConvertToRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String[] player;
    private final String query;
    private final String convert;

    public ConvertToRunnable(String newType, Playtime p, String inQuery, String... players) {
        convert = newType;
        plugin = p;
        query = inQuery;
        player = players;
    }

    public void run() {
        if (convert.equals("mysql")) {
            plugin.getConfigurationLoader().getConfig().set("data.manager", "mysql");
            plugin.getConfigurationLoader().saveConfig();
            DataManager dm = new DataManager(plugin);
            dm.select("mysql");
            dm.setup();

            MySQL db = new MySQL();
            try {
                db.open();
                db.update("TRUNCATE TABLE `playTime`");
                db.update(query);
                db.close();
            } catch (SQLException e) {
                if (plugin.getDebug() == 3) {
                    e.printStackTrace();
                }
            }
            dm.getDataHandler().cleanup();
            plugin.reload();
            plugin.setBusy(false);
        } else if (convert.equals("sqlite")) {
            plugin.getConfigurationLoader().getConfig().set("data.manager", "sqlite");
            plugin.getConfigurationLoader().saveConfig();
            DataManager dm = new DataManager(plugin);
            dm.select("sqlite");
            dm.setup();

            SQLite db = new SQLite();
            try {
                db.open();
                db.update("DELETE FROM `playTime`");
                String[] queries = query.split("\n");
                for (String p : player) {
                    Bukkit.getScheduler().callSyncMethod(plugin, new SendMessageCallable(p, "[&ePlaytime&f] &6Adding " + queries.length + " rows to SQLite database. Estimated time: " + getTime(queries.length)));
                }
                for (String s : queries) {
                    db.update(s);
                }
                db.close();
            } catch (SQLException e) {
                if (plugin.getDebug() == 3) {
                    e.printStackTrace();
                }
            }
            dm.getDataHandler().cleanup();
            plugin.reload();
            plugin.setBusy(false);
            for (String p : player) {
                Bukkit.getScheduler().callSyncMethod(plugin, new SendMessageCallable(p, "[&ePlaytime&f] &6Conversion complete!"));
            }
        } else if (convert.equals("flatfile")) {
            for (String s : player) {
                plugin.getServer().getScheduler().callSyncMethod(plugin, new SendMessageCallable(s, "You cannot convert to flat file storage!"));
            }
        }
    }
    
    private String getTime(int rows) {
        long time = Math.round(rows / (369 + ((2 / 3) - 0.2)));
        long seconds = time % 60;
        long minutes = time / 60;
        return ((minutes >= 1) ? ((minutes != 1) ? minutes + " minutes" : minutes + " minute") : "") + " " + ((seconds != 1) ? seconds + " seconds." : seconds + " second.");
    }
}
