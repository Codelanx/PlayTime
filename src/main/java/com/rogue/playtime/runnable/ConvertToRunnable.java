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

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ConvertToRunnable implements Runnable {

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
        DataManager dm = new DataManager(plugin, false);
        if (convert.equals("mysql")) {
            plugin.getConfigurationLoader().getConfig().set("data.manager", convert);
            plugin.getConfigurationLoader().saveConfig();
            dm.select(convert);
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
            plugin.reload();
        } else if (convert.equals("sqlite")) {
            plugin.getConfigurationLoader().getConfig().set("data.manager", convert);
            plugin.getConfigurationLoader().saveConfig();
            dm.select(convert);
            dm.setup();

            SQLite db = new SQLite();
            try {
                db.open();
                db.update("DELETE FROM `playTime`");
                String[] queries = query.split("\n");
                for (String p : player) {
                    plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, plugin.getCipher().getString("runnable.convertto.rows", queries.length, getTime(queries.length))), 0L);
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
            plugin.reload();
            for (String p : player) {
                plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, plugin.getCipher().getString("runnable.convertto.complete")), 0L);
            }
        } else if (convert.equals("flatfile")) {
            for (String s : player) {
                plugin.getExecutiveManager().runCallable(new SendMessageCallable(s, plugin.getCipher().getString("runnable.convertto.noflat")), 0L);
            }
        }
        plugin.setBusy(false);
    }
    
    /**
     * Returns an estimation of how long SQLite conversion will take based on
     * the number of rows required to add. The converter uses individual INSERT
     * statements for each row, which is unfortunate due to SQLite's limitations
     * on multiple values within inserts or union selects.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param rows The number of rows to evaluate
     * @return The estimated time as a readable string
     */
    private String getTime(int rows) {
        long time = Math.round(rows / (369 + ((2 / 3) - 0.2)));
        long seconds = time % 60;
        long minutes = time / 60;
        return ((minutes >= 1) ? ((minutes != 1) ? minutes + " " + plugin.getCipher().getString("variables.minutes") : minutes + " " + plugin.getCipher().getString("variables.minute")) : "") + " " + ((seconds != 1) ? seconds + " " + plugin.getCipher().getString("variables.seconds") + "." : seconds + " " + plugin.getCipher().getString("variables.second") + ".");
    }
}
