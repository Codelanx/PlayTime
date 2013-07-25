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
package com.rogue.playtime.data.mysql;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.DataHandler;
import com.rogue.playtime.runnable.MySQLAddRunnable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @since
 * @author 1Rogue
 * @version
 */
public class Data_MySQL implements DataHandler {
    
    private BukkitTask updater;
    private Playtime plugin = Playtime.getPlugin();
    private MySQL db;

    public int getPlaytime(String username) {
        db = new MySQL();
        int ret = 0;
        try {
            db = new MySQL();
            db.open();
            ResultSet result = db.query("SELECT `playtime` FROM `playTime` WHERE `username`='" + username + "'");
            result.first();
            ret = result.getInt(1);
        } catch (SQLException e) {
            if (Playtime.getPlugin().getDebug() == 3) {
                e.printStackTrace();
            }
        }
        try {
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public int getDeathtime(String username) {
        int ret = 0;
        try {
            db = new MySQL();
            db.open();
            ResultSet result = db.query("SELECT `deathtime` FROM `playTime` WHERE `username`='" + username + "'");
            result.first();
            ret = result.getInt(1);
        } catch (SQLException e) {
            if (Playtime.getPlugin().getDebug() == 3) {
                e.printStackTrace();
            }
        }
        try {
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public void verifyFormat() {
         db = new MySQL();
        plugin.getLogger().info("Connecting to MySQL database...");
        try {
            db.open();
            if (db.checkConnection()) {
                plugin.getLogger().info("Successfully connected to database!");
                if (!db.checkTable("playTime")) {
                    plugin.getLogger().log(Level.INFO, "Creating table ''playTime'' in database {0}", SQL_Vars.DATABASE);
                    ResultSet result = db.query("CREATE TABLE playTime ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL DEFAULT 0, deathtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    result.close();
                } else {
                    try {
                        db.update("ALTER TABLE `playTime` ADD COLUMN `username` VARCHAR(32) NULL DEFAULT NULL AFTER `id`, ADD UNIQUE INDEX `username` (`username`)");
                        plugin.getLogger().info("Missing username column! Recreating table...");
                        db.update("DROP TABLE `playTime`");
                        db.update("CREATE TABLE playTime ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL, deathtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        plugin.getLogger().info("Missing playtime column! Repairing...");
                        
                        plugin.getLogger().info("Playtime values reset to 0.");
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD UNIQUE INDEX `username` (`username`)");
                        plugin.getLogger().info("Updating SQL table for 1.1");
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD deathtime int NOT NULL AFTER `playtime`");
                        plugin.getLogger().info("Updating SQL table for 1.2.0");
                    } catch (SQLException e) {
                    }
                    plugin.getLogger().info("SQL table is up to date!");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setup() {
        SQL_Vars.HOST = plugin.getConfig().getString("managers.mysql.host");
        SQL_Vars.DATABASE = plugin.getConfig().getString("managers.mysql.database");
        SQL_Vars.USER = plugin.getConfig().getString("managers.mysql.username");
        SQL_Vars.PASS = plugin.getConfig().getString("managers.mysql.password");
        SQL_Vars.PORT = plugin.getConfig().getString("managers.mysql.port");
    }

    public void initiateRunnable() {
        try {
            if (db.checkConnection()) {
                updater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new MySQLAddRunnable(plugin), 1200L, 1200L);
                db.close();
            } else {
                plugin.getLogger().info("Error connecting to MySQL database... shutting down!");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void cleanup() {
        updater.cancel();
        updater = null;
        db = null;
    }

}
