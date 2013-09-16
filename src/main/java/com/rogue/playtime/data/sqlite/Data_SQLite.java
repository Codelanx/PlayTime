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
import com.rogue.playtime.runnable.StartConvertRunnable;
import com.rogue.playtime.runnable.AddRunnable;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 * SQLite Data Manager. See DataHandler for information on each method.
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class Data_SQLite implements DataHandler {

    private Playtime plugin;
    private SQLite db;
    
    public Data_SQLite(Playtime plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return "sqlite";
    }

    public int getValue(String data, String username) {
        username = this.plugin.getBestPlayer(username);
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        this.db = new SQLite();
        int ret = 0;
        try {
            this.db.open();
            ResultSet result = this.db.query("SELECT `" + data + "` FROM `playTime` WHERE `username`='" + username + "'");
            if (result.next()) {
                ret = result.getInt(1);
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
        } finally {
            this.db.close();
        }
        return ret;
    }

    public Map<String, Integer> getTopPlayers(String data, int amount) {
        this.db = new SQLite();
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
            this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
        } finally {
            this.db.close();
        }
        return players;
    }

    public Map<String, Integer> getPlayersInRange(String timer, int minimum, int maximum) {
        this.db = new SQLite();
        Map<String, Integer> back = new HashMap();
        try {
            this.db.open();
            ResultSet ret = this.db.query("SELECT `username` FROM `playTime` WHERE `" + timer + "` BETWEEN " + minimum + " AND " + maximum);
            while (ret.next()) {
                back.put(ret.getString("username"), ret.getInt(timer));
            }
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
        } finally {
            this.db.close();
        }
        return back;
    }

    public void verifyFormat() {
        this.db = new SQLite();
        this.plugin.getLogger().info(this.plugin.getCipher().getString("data.sqlite.main.connecting"));
        try {
            this.db.open();
            this.plugin.getLogger().info(this.plugin.getCipher().getString("data.sqlite.main.connect-success"));
            if (!this.db.checkTable("playTime")) {
                this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("data.sqlite.main.create-table"));
                this.db.update("CREATE TABLE playTime ( id INTEGER NOT NULL PRIMARY KEY, username VARCHAR(32) NOT NULL UNIQUE, playtime INTEGER NOT NULL DEFAULT 0, deathtime INTEGER NOT NULL DEFAULT 0, onlinetime INTEGER NOT NULL DEFAULT 0)");
            } else {
                if (this.plugin.firstRun()) {
                    try {
                        this.db.update("UPDATE `playTime` SET `onlinetime`=0");
                        if (this.plugin.getDebug() >= 1) {
                            this.plugin.getLogger().info(this.plugin.getCipher().getString("data.sqlite.main.reset-column", "`onlinetime`"));
                        }
                    } catch (SQLException e) {
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, this.plugin.getCipher().getString("data.sqlite.main.error"), ex);
            File file = new File(this.plugin.getDataFolder() + File.separator + "users.db");
            file.delete();
            Bukkit.getServer().getPluginManager().disablePlugin(this.plugin);
        } finally {
            this.db.close();
        }
    }

    public void init() {
    }

    public void startRunnables() {
        this.plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(this.plugin), 60L, 60L);
    }

    public void startConversion(String newType, String... players) {
        this.plugin.onDisable();
        this.plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(this.plugin, newType, players), 0L);
    }

    public void cleanup() {
        this.db = null;
    }
}
