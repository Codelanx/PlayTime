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
import com.rogue.playtime.data.DataManager;
import com.rogue.playtime.data.mysql.MySQL;
import java.sql.SQLException;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class MySQLConvertToRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String[] player;
    private final String query;

    public MySQLConvertToRunnable(Playtime p, String inQuery, String... players) {
        plugin = p;
        query = inQuery;
        player = players;
    }

    public void run() {
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
    }
}
