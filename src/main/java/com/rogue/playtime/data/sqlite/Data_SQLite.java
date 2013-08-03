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
import com.rogue.playtime.runnable.ResetRunnable;
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

    private Playtime plugin = Playtime.getPlugin();
    private SQLite db;

    public String getName() {
        return "sqlite";
    }

    public int getValue(String data, String username) {
        username = plugin.getBestPlayer(username);
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        db = new SQLite();
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
        db = new SQLite();
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
        db = new SQLite();
        Map<String, Integer> back = new HashMap();
        try {
            db.open();
            ResultSet ret = db.query("SELECT `username` FROM `playTime` WHERE `" + timer + "` BETWEEN " + minimum + " AND " + maximum);
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

    public void onDeath(String username) {
        plugin.getExecutiveManager().runAsyncTask(new ResetRunnable(plugin, username, "deathtime"), 0L);
    }

    public void onLogout(String username) {
        plugin.getExecutiveManager().runAsyncTask(new ResetRunnable(plugin, username, "onlinetime"), 0L);
    }

    public void verifyFormat() {
        db = new SQLite();
        plugin.getLogger().info(plugin.getCipher().getString("data.sqlite.main.connecting"));
        try {
            db.open();
            plugin.getLogger().info(plugin.getCipher().getString("data.sqlite.main.connect-success"));
            if (!db.checkTable("playTime")) {
                plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("data.sqlite.main.create-table"));
                db.update("CREATE TABLE playTime ( id INTEGER NOT NULL PRIMARY KEY, username VARCHAR(32) NOT NULL UNIQUE, playtime INTEGER NOT NULL DEFAULT 0, deathtime INTEGER NOT NULL DEFAULT 0, onlinetime INTEGER NOT NULL DEFAULT 0)");
            } else {
                try {
                    db.update("UPDATE `playTime` SET `onlinetime`=0");
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info(plugin.getCipher().getString("data.sqlite.reset-column", "`onlinetime`"));
                    }
                } catch (SQLException e) {
                }
            }
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, plugin.getCipher().getString("data.sqlite.main.error"), ex);
            File file = new File(plugin.getDataFolder() + File.separator + "users.db");
            file.delete();
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void init() {
    }

    public void startRunnables() {
        plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(plugin), 60L, 60L);
    }

    public void startConversion(String newType, String... players) {
        plugin.onDisable();
        plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(plugin, newType, players), 0L);
    }

    public void cleanup() {
        db = null;
    }
}
