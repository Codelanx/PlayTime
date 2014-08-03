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
package com.codelanx.playtime.runnable;

import com.codelanx.playtime.Playtime;
import com.codelanx.playtime.callable.SendMessageCallable;
import com.codelanx.playtime.data.mysql.MySQL;
import com.codelanx.playtime.data.sqlite.SQLite;
import com.codelanx.playtime.data.yaml.YAML;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class StartConvertRunnable implements Runnable {

    private final Playtime plugin;
    private final String newType;
    private final String[] players;

    public StartConvertRunnable(Playtime plugin, String newType, String... players) {
        this.plugin = plugin;
        this.newType = newType;
        this.players = players;
    }

    public void run() {
        String current = this.plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            MySQL db = new MySQL();
            StringBuilder sb = new StringBuilder();
            for (String p : this.players) {
                this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, this.plugin.getCipher().getString("runnable.startconvert.mysql.download")), 0L);
            }
            this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.startconvert.mysql.download"));
            try {
                db.open();

                ResultSet ret = db.query("SELECT * FROM `playTime`");
                int i = 1;
                while (ret.next()) {
                    sb.append("INSERT OR IGNORE INTO `playTime` (`id`, `username`, `uuid`, `playtime`, `deathtime`, `onlinetime`) VALUES (")
                            .append(i++).append(", '").append(ret.getString(2)).append("', '").append(ret.getString(3)).append("', ")
                            .append(ret.getInt(4)).append(", ").append(ret.getInt(5)).append(", ").append(ret.getInt(6)).append(");\n");
                }
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
            } finally {
                db.close();
            }
            this.plugin.getDataManager().convertTo(this.newType, sb.substring(0, sb.length() - 1), this.players);
        } else if (current.equals("sqlite")) {
            SQLite db = new SQLite();
            StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`, `uuid`, `playtime`, `deathtime`, `onlinetime`) VALUES ");
            try {
                db.open();
                ResultSet ret = db.query("SELECT * FROM `playTime`");
                int i = 1;
                while (ret.next()) {
                    sb.append("('").append(ret.getString(2)).append("', '").append(ret.getString(3)).append("', ")
                            .append(ret.getInt(4)).append(", ").append(ret.getInt(5)).append(", ").append(ret.getInt(6)).append("), ");
                }
                ret.close();
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
            } finally {
                db.close();
            }
            this.plugin.getDataManager().convertTo(this.newType, sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`", this.players);
        } else if (current.equals("flatfile")) {
            this.plugin.getLogger().log(Level.SEVERE, "{0} attempted to run YML check, which is broken!", this.getClass().getSimpleName());
            /*YAML yaml = new YAML();

            ConfigurationSection users = yaml.getFile().getConfigurationSection("users");
            int i = 1;
            StringBuilder sb;
            String out = "null";
            if (this.newType.equals("mysql")) {
                sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`, `deathtime`, `onlinetime`) VALUES ");
                for (String s : users.getKeys(false)) {
                    sb.append("('").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append("), ");
                }
                out = sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`";

            } else if (this.newType.equals("sqlite")) {
                sb = new StringBuilder("INSERT INTO `playTime` ");
                for (String s : users.getKeys(false)) {
                    sb.append("INSERT OR IGNORE INTO `playTime` (`id`, `username`, `playtime`, `deathtime`, `onlinetime`) VALUES (").append(i).append(", '").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append(");\n");
                }
                out = sb.substring(0, sb.length() - 1);
            }
            if (!out.equals("null")) {
                this.plugin.getDataManager().convertTo(this.newType, out, this.players);
            }*/
        }
    }
}
