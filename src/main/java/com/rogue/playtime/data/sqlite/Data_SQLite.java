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
package com.rogue.playtime.data.sqlite;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.DataHandler;
import com.rogue.playtime.runnable.sqlite.SQLiteAddRunnable;
import com.rogue.playtime.runnable.sqlite.SQLiteDeathRunnable;
import java.io.File;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @since @author 1Rogue
 * @version
 */
public class Data_SQLite implements DataHandler {

    private BukkitTask updater;
    private Playtime plugin = Playtime.getPlugin();
    private SQLite db;

    public int getPlaytime(String username) {
        db = new SQLite();
        int ret = 0;
        try {
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
        db = new SQLite();
        int ret = 0;
        try {
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

    public void onDeath(String username) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new SQLiteDeathRunnable(username));
    }

    public void verifyFormat() {
        db = new SQLite();
        plugin.getLogger().info("Connecting to SQLite database...");
        try {
            db.open();
            plugin.getLogger().info("Successfully connected to database!");
            if (!db.checkTable("playTime")) {
                plugin.getLogger().log(Level.INFO, "Creating table ''playTime'' in database {0}", SQLite_Vars.DATABASE);
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
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setup() {
        SQLite_Vars.DATABASE = plugin.getConfig().getString("managers.sqlite.database");

        File data = new File(plugin.getDataFolder() + File.separator + "users.db");
        if (!data.exists()) {
            try {
                Driver d = (Driver) Class.forName("org.sqlite.JDBC").newInstance();
                DriverManager.registerDriver(d);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Error loading database driver: {0}", e.toString());
            }
        }
    }

    public void initiateRunnable() {
        updater = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new SQLiteAddRunnable(plugin), 1200L, 1200L);
    }

    public void cleanup() {
        updater.cancel();
        updater = null;
        db = null;
    }
}
