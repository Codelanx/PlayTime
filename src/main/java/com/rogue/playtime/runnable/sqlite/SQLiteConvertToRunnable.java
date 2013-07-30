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
import com.rogue.playtime.data.DataManager;
import com.rogue.playtime.data.sqlite.SQLite;
import java.sql.SQLException;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class SQLiteConvertToRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String[] player;
    private final String query;
    
    public SQLiteConvertToRunnable(Playtime p, String inQuery, String... players) {
        plugin = p;
        query = inQuery;
        player = players;
    }

    public void run() {
        plugin.getConfigurationLoader().getConfig().set("data.manager", "sqlite");
        plugin.getConfigurationLoader().saveConfig();
        DataManager dm = new DataManager(plugin);
        dm.select("sqlite");
        dm.setup();

        SQLite db = new SQLite();
        try {
            db.open();
            db.update("DELETE FROM `playTime`");
            db.update(query);
            db.close();
        } catch (SQLException e) {
            if (plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        }
        dm.getDataHandler().cleanup();
        plugin.reload();
    }
}
