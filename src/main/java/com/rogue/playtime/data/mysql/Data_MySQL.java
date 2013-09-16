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

    private Playtime plugin;
    private MySQL db;
    
    public Data_MySQL(Playtime plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return "mysql";
    }

    public int getValue(String data, String username) {
        username = this.plugin.getBestPlayer(username);
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        this.db = new MySQL();
        int ret = 0;
        try {
            this.db.open();
            ResultSet result = this.db.query("SELECT `" + data + "` FROM `playTime` WHERE `username`='" + username + "'");
            if (result.next()) {
                ret = result.getInt(1);
            }
        } catch (SQLException e) {
            if (this.plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        } finally {
            this.db.close();
        }
        return ret;
    }

    public Map<String, Integer> getTopPlayers(String data, int amount) {
        this.db = new MySQL();
        Map<String, Integer> players = new HashMap();
        try {
            this.db.open();
            ResultSet result = this.db.query("SELECT * FROM `playTime` ORDER BY `" + data + "` DESC LIMIT " + amount);
            boolean end = false;
            while (!end) {
                if (result.next()) {
                    players.put(result.getString("username"), result.getInt(data));
                } else {
                    end = true;
                }
            }
        } catch (SQLException e) {
            if (this.plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        } finally {
            this.db.close();
        }
        return players;
    }

    public Map<String, Integer> getPlayersInRange(String timer, int minimum, int maximum) {
        this.db = new MySQL();
        Map<String, Integer> back = new HashMap();
        try {
            this.db.open();
            ResultSet ret = this.db.query("SELECT * FROM `playTime` WHERE `" + timer + "` BETWEEN " + minimum + " AND " + maximum);
            while (ret.next()) {
                back.put(ret.getString("username"), ret.getInt(timer));
            }
        } catch (SQLException e) {
            if (this.plugin.getDebug() == 3) {
                e.printStackTrace();
            }
        } finally {
            this.db.close();
        }
        return back;
    }

    public void verifyFormat() {
        this.db = new MySQL();
        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.connecting"));
        try {
            this.db.open();
            if (this.db.checkConnection()) {
                this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.connect-success"));
                if (!this.db.checkTable("playTime")) {
                    this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("data.mysql.main.create-table", this.plugin.getConfig().getString("managers.mysql.database")));
                    int result = this.db.update("CREATE TABLE `playTime` ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL DEFAULT 0, deathtime int NOT NULL DEFAULT 0, onlinetime int NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE KEY (username)) ENGINE=MyISAM;");
                } else {
                    try {
                        this.db.update("ALTER TABLE `playTime` ADD COLUMN `username` VARCHAR(32) NULL DEFAULT NULL AFTER `id`, ADD UNIQUE INDEX `username` (`username`)");
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.missing-user"));
                        this.db.update("DROP TABLE `playTime`");
                        this.db.update("CREATE TABLE playTime ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL, deathtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` ADD `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.missing-playtime"));
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.reset-column", "`playtime`"));
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` ADD UNIQUE INDEX `username` (`username`)");
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.updating-table", "1.1"));
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` ADD deathtime int NOT NULL AFTER `playtime`");
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.updating-table", "1.2.0"));
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` ADD onlinetime int NOT NULL DEFAULT 1 AFTER `deathtime`");
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.updating-table", "1.3.0"));
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` CHANGE COLUMN `playtime` `playtime` int NOT NULL DEFAULT 0 AFTER `username`");
                        if (this.plugin.getDebug() >= 1) {
                            this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.setting-defaults", "`playtime`"));
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` CHANGE COLUMN `deathtime` `deathtime` int NOT NULL DEFAULT 0 AFTER `playtime`");
                        if (this.plugin.getDebug() >= 1) {
                            this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.setting-defaults", "`deathtime`"));
                        }
                    } catch (SQLException e) {
                    }
                    try {
                        this.db.update("ALTER TABLE `playTime` CHANGE COLUMN `onlinetime` `onlinetime` int NOT NULL DEFAULT 0 AFTER `deathtime`");
                        if (this.plugin.getDebug() >= 1) {
                            this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.setting-defaults", "`onlinetime`"));
                        }
                    } catch (SQLException e) {
                    }
                    if (this.plugin.firstRun()) {
                        try {
                            this.db.update("UPDATE `playTime` SET `onlinetime`=0");
                            if (this.plugin.getDebug() >= 1) {
                                this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.reset-column", "`onlinetime`"));
                            }
                        } catch (SQLException e) {
                        }
                    }
                    this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.uptodate"));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.db.close();
        }
    }

    public void init() {
        this.db = new MySQL(this.plugin.getConfig().getString("managers.mysql.host"), this.plugin.getConfig().getString("managers.mysql.username"), this.plugin.getConfig().getString("managers.mysql.password"), this.plugin.getConfig().getString("managers.mysql.database"), this.plugin.getConfig().getString("managers.mysql.port"));
    }

    public void startRunnables() {
        this.db = new MySQL();
        try {
            this.db.open();
            if (this.db.checkConnection()) {
                this.plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(this.plugin), 60L, 60L);
            } else {
                this.plugin.getLogger().info(this.plugin.getCipher().getString("data.mysql.main.error"));
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            this.db.close();
        }
    }

    public void startConversion(String newType, String... players) {
        this.plugin.onDisable();
        this.plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(this.plugin, newType, players), 0L);
    }

    public void cleanup() {
        this.db = null;
    }
}
