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
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.data.sqlite.SQLite;
import com.rogue.playtime.data.yaml.YAML;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class StartConvertRunnable implements Runnable {

    private Playtime plugin;
    private final String convertTo;
    private final String[] player;

    public StartConvertRunnable(Playtime p, String newType, String... players) {
        plugin = p;
        convertTo = newType;
        player = players;
    }

    public void run() {
        String current = plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            MySQL db = new MySQL();
            StringBuilder sb = new StringBuilder();
            for (String p : player) {
                plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, plugin.getCipher().getString("runnable.startconvert.mysql.download")), 0L);
            }
            plugin.getLogger().info(plugin.getCipher().getString("runnable.startconvert.mysql.download"));
            try {
                db.open();

                ResultSet ret = db.query("SELECT * FROM `playTime`");
                int i = 1;
                while (ret.next()) {
                    sb.append("INSERT OR IGNORE INTO `playTime` (`id`, `username`, `playtime`, `deathtime`, `onlinetime`) VALUES (").append(i++).append(", '").append(ret.getString(2)).append("', ").append(ret.getInt(3)).append(", ").append(ret.getInt(4)).append(", ").append(ret.getInt(5)).append(");\n");
                }
                db.close();
            } catch (SQLException e) {
                if (plugin.getDebug() == 3) {
                    e.printStackTrace();
                }
            }
            plugin.getDataManager().convertTo(convertTo, sb.substring(0, sb.length() - 1), player);
        } else if (current.equals("sqlite")) {
            SQLite db = new SQLite();
            StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`, `deathtime`, `onlinetime`) VALUES ");
            try {
                db.open();
                ResultSet ret = db.query("SELECT * FROM `playTime`");
                int i = 1;
                while (ret.next()) {
                    sb.append("('").append(ret.getString(2)).append("', ").append(ret.getInt(3)).append(", ").append(ret.getInt(4)).append(", ").append(ret.getInt(5)).append("), ");
                }
                ret.close();
                db.close();
            } catch (SQLException e) {
                if (plugin.getDebug() == 3) {
                    e.printStackTrace();
                }
            }
            plugin.getDataManager().convertTo(convertTo, sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`", player);
        } else if (current.equals("flatfile")) {
            YAML yaml = new YAML();

            ConfigurationSection users = yaml.getFile().getConfigurationSection("users");
            int i = 1;
            StringBuilder sb;
            String out = "null";
            if (convertTo.equals("mysql")) {
                sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`, `deathtime`, `onlinetime`) VALUES ");
                for (String s : users.getKeys(false)) {
                    sb.append("('").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append("), ");
                }
                out = sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`";

            } else if (convertTo.equals("sqlite")) {
                sb = new StringBuilder("INSERT INTO `playTime` ");
                for (String s : users.getKeys(false)) {
                    sb.append("INSERT OR IGNORE INTO `playTime` (`id`, `username`, `playtime`, `deathtime`, `onlinetime`) VALUES (").append(i).append(", '").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append(");\n");
                }
                out = sb.substring(0, sb.length() - 1);
            }
            if (!out.equals("null")) {
                plugin.getDataManager().convertTo(convertTo, out, player);
            }
        }
    }
}
