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
import com.rogue.playtime.data.mysql.MySQL;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class MySQLStartConvertRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String convertTo;
    private final String[] player;

    public MySQLStartConvertRunnable(Playtime p, String newType, String... players) {
        plugin = p;
        convertTo = newType;
        player = players;
    }

    public void run() {
        MySQL db = new MySQL();
        StringBuilder sb = new StringBuilder("INSERT INTO `playTime` ");
        try {
            db.open();
            
            ResultSet ret = db.query("SELECT * FROM `playTime`");
            int i = 1;
            while (ret.next()) {
                if (i > 1) {
                    sb.append("UNION SELECT ").append(i).append(", '").append(ret.getString(2)).append("', ").append(ret.getInt(3)).append(", ").append(ret.getInt(4)).append(", ").append(ret.getInt(5)).append(" ");
                } else {
                    sb.append("SELECT ").append(i).append(" AS 'column1', '").append(ret.getString(2)).append("' AS 'column2', ").append(ret.getInt(3)).append(" AS 'column3', ").append(ret.getInt(4)).append(" AS 'column4', ").append(ret.getInt(5)).append(" AS 'column5' ");
                }
                i++;
            }
            db.close();
        } catch (SQLException e) {
            if (plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        }
        plugin.getDataManager().convertTo(convertTo, sb.substring(0, sb.length() - 1), player);
    }
}
