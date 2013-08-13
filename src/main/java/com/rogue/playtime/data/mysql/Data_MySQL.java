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
import com.rogue.playtime.runnable.AddRunnable;
import com.rogue.playtime.runnable.StartConvertRunnable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 * MySQL Data manager. See DataHandler for information about each method.
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class Data_MySQL implements DataHandler {

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

    public Map<String, Integer> getPlayersInRange(String timer, int minimum, int maximum) {
        db = new MySQL();
        Map<String, Integer> back = new HashMap();
            try {
            db.open();
            ResultSet ret = db.query("SELECT * FROM `playTime` WHERE `" + timer + "` BETWEEN " + minimum + " AND " + maximum);
            while (ret.next()) {
                back.put(ret.getString("username"), ret.getInt(timer));
            }
        } catch (SQLException e) {
            if (plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        }
        return back;
    }

    public void verifyFormat() {
        db = new MySQL();
        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.connecting"));
        try {
            db.open();
            if (db.checkConnection()) {
                plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.connect-success"));
                if (!db.checkTable("playTime")) {
                    plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("data.mysql.create-table", MySQL_Vars.DATABASE));
                    int result = db.update("CREATE TABLE `playTime` ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL DEFAULT 0, deathtime int NOT NULL DEFAULT 0, onlinetime int NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE KEY (username)) ENGINE=MyISAM;");
                } else {
                    try {
                        db.update("ALTER TABLE `playTime` ADD COLUMN `username` VARCHAR(32) NULL DEFAULT NULL AFTER `id`, ADD UNIQUE INDEX `username` (`username`)");
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.missing-user"));
                        db.update("DROP TABLE `playTime`");
                        db.update("CREATE TABLE playTime ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL, deathtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.missing-playtime"));
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.reset-column", "`playtime`"));
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD UNIQUE INDEX `username` (`username`)");
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.updating-table", "1.1"));
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD deathtime int NOT NULL AFTER `playtime`");
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.updating-table", "1.2.0"));
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` ADD onlinetime int NOT NULL DEFAULT 1 AFTER `deathtime`");
                        plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.updating-table", "1.3.0"));
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `playtime` `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.setting-defaults", "`playtime`"));
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `deathtime` `deathtime` int NOT NULL DEFAULT 0 AFTER `playtime`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.setting-defaults", "`deathtime`"));
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        db.update("ALTER TABLE `playTime` CHANGE COLUMN `onlinetime` `onlinetime` int NOT NULL DEFAULT 0 AFTER `deathtime`");
                        if (plugin.getDebug() >= 1) {
                            plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.setting-defaults", "`onlinetime`"));
                        }
                    } catch (SQLException e) {
                    }
                    if (plugin.firstRun()) {
                        try {
                            db.update("UPDATE `playTime` SET `onlinetime`=0");
                            if (plugin.getDebug() >= 1) {
                                plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.reset-column", "`onlinetime`"));
                            }
                        } catch (SQLException e) {
                        }
                    }
                    plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.uptodate"));
                }
            }
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void init() {
        MySQL_Vars.HOST = plugin.getConfig().getString("managers.mysql.host");
        MySQL_Vars.DATABASE = plugin.getConfig().getString("managers.mysql.database");
        MySQL_Vars.USER = plugin.getConfig().getString("managers.mysql.username");
        MySQL_Vars.PASS = plugin.getConfig().getString("managers.mysql.password");
        MySQL_Vars.PORT = plugin.getConfig().getString("managers.mysql.port");
    }

    public void startRunnables() {
        db = new MySQL();
        try {
            db.open();
            if (db.checkConnection()) {
                plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(plugin), 60L, 60L);
                db.close();
            } else {
                plugin.getLogger().info(plugin.getCipher().getString("data.mysql.main.error"));
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startConversion(String newType, String... players) {
        plugin.onDisable();
        plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(plugin, newType, players), 0L);
    }

    public void cleanup() {
        db = null;
    }
}
