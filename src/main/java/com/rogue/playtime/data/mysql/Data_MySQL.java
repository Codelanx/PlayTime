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
import com.rogue.playtime.runnable.mysql.MySQLAddRunnable;
import com.rogue.playtime.runnable.mysql.MySQLResetRunnable;
import com.rogue.playtime.runnable.mysql.MySQLStartConvertRunnable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * MySQL Data manager. See DataHandler for information about each method.
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class Data_MySQL implements DataHandler {

    private BukkitTask updater;
    private Playtime plugin = Playtime.getPlugin();
    private MySQL db;
    
    public String getName() {
        return "mysql";
    }

    public int getValue(String data, String username) {
        username = plugin.getBestPlayer(username);
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        db = new MySQL();
        int ret = 0;
        try {
            db.open();
            ResultSet result = db.query("SELECT `" + data + "` FROM `playTime` WHERE `username`='" + username + "'");
            if (result.next()) {
                ret = result.getInt(1);
            }
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

    public Map<String, Integer> getTopPlayers(String data, int amount) {
        db = new MySQL();
        Map<String, Integer> players = new HashMap();
        try {
            db.open();
            ResultSet result = db.query("SELECT * FROM `playTime` ORDER BY `" + data + "` DESC LIMIT " + amount);
            boolean end = false;
            while (!end) {
                if (result.next()) {
                    players.put(result.getString("username"), result.getInt(data));
                } else {
                    end = true;
                }
            }
            db.close();
        } catch (SQLException e) {
            if (Playtime.getPlugin().getDebug() == 3) {
                e.printStackTrace();
            }
        }
        return players;
    }

    public void onDeath(String username) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new MySQLResetRunnable(username, "deathtime"));
    }

    public void onLogout(String username) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new MySQLResetRunnable(username, "onlinetime"));
    }

    public void verifyFormat() {
        db = new MySQL();
        plugin.getLogger().info("Connecting to MySQL database...");
        try {
            db.open();
            if (db.checkConnection()) {
                plugin.getLogger().info("Successfully connected to database!");
                if (!db.checkTable("playTime")) {
                    plugin.getLogger().log(Level.INFO, "Creating table 'playTime' in database {0}", MySQL_Vars.DATABASE);
                    int result = db.update("CREATE TABLE `playTime` ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL DEFAULT 0, deathtime int NOT NULL DEFAULT 0, onlinetime int NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE KEY (username)) ENGINE=MyISAM;");
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
                    try {
                        db.update("ALTER TABLE `playTime` ADD onlinetime int NOT NULL DEFAULT 1 AFTER `deathtime`");
                        plugin.getLogger().info("Updating SQL table for 1.3.0");
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `playtime` `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info("Setting defaults for column `playtime`");
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `deathtime` `deathtime` int NOT NULL DEFAULT 0 AFTER `playtime`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info("Setting defaults for column `deathtime`");
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `onlinetime` `onlinetime` int NOT NULL DEFAULT 0 AFTER `deathtime`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info("Setting defaults for column `onlinetime`");
                        }
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
        MySQL_Vars.HOST = plugin.getConfig().getString("managers.mysql.host");
        MySQL_Vars.DATABASE = plugin.getConfig().getString("managers.mysql.database");
        MySQL_Vars.USER = plugin.getConfig().getString("managers.mysql.username");
        MySQL_Vars.PASS = plugin.getConfig().getString("managers.mysql.password");
        MySQL_Vars.PORT = plugin.getConfig().getString("managers.mysql.port");
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
    
    public void startConversion(String newType, String... players) {
        plugin.onDisable();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new MySQLStartConvertRunnable(plugin, newType, players));
    }

    public void cleanup() {
        if (updater != null) {
            updater.cancel();
        }
        updater = null;
        db = null;
    }
}
