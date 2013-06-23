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
package com.rogue.playtime;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Playtime extends JavaPlugin {

    protected MySQL db;
    protected BukkitTask updater;
    protected int debug = 0;

    @Override
    public void onEnable() {
        final long startTime = System.nanoTime();

        File file = new File(getDataFolder() + File.separator + "config.yml");

        if (file.exists() && this.getConfig().getBoolean("debug")) {
            this.getLogger().info("Debug mode enabled! Prepare for a lot of spam!");
        }
        if (!file.exists()) {
            this.getLogger().info("Generating first time config.yml...");
            this.getConfig().addDefault("debug-level", "0");
            this.getConfig().addDefault("mysql.host", "localhost");
            this.getConfig().addDefault("mysql.port", "3306");
            this.getConfig().addDefault("mysql.database", "minecraft");
            this.getConfig().addDefault("mysql.username", "root");
            this.getConfig().addDefault("mysql.password", "password");
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
        }

        debug = this.getConfig().getInt("debug");
        if (debug > 3) {
            debug = 3;
        }
        if (debug < 0) {
            debug = 0;
        }

        setupDatabase();

        try {
            if (db.checkConnection()) {
                updater = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AddRunnable(this), 1200L, 1200L);
                db.close();
            } else {
                this.getLogger().info("Error connecting to MySQL database... shutting down!");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }

        final long endTime = System.nanoTime();
        if (file.exists() && debug >= 1) {
            final long duration = endTime - startTime;
            this.getLogger().log(Level.INFO, "Enabled ({0})", this.readableProfile(duration));
        }
    }

    @Override
    public void onDisable() {
        updater.cancel();
        try {
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
        db = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("playtime")) {
            if (args.length == 0 && sender instanceof Player) {
                if (sender.hasPermission("playtime.use")) {
                    int time = getTime(sender.getName());
                    int minutes = time % 60;
                    if (time >= 60) {
                        int hours = time / 60;
                        sender.sendMessage(ChatColor.GOLD + "You have played for " + hours + " hour" + (hours == 1 ? "" : "s") + " and " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + "You have played for " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                    }
                } else {
                    sender.sendMessage("[" + ChatColor.YELLOW + "PlayTime" + ChatColor.RESET + "] " + ChatColor.GOLD + "You do not have permission to do that!");
                }
            } else if (args.length == 1) {
                if (sender.hasPermission("playtime.use.others")) {
                    int time = getTime(args[0]);
                    int minutes = time % 60;
                    if (time >= 60) {
                        int hours = time / 60;
                        sender.sendMessage(ChatColor.GOLD + args[0] + " has played for " + hours + " hour" + (hours == 1 ? "" : "s") + " and " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                    } else {
                        sender.sendMessage(ChatColor.GOLD + args[0] + " has played for " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                    }
                } else {
                    sender.sendMessage("[" + ChatColor.YELLOW + "PlayTime" + ChatColor.RESET + "] " + ChatColor.GOLD + "You do not have permission to do that!");
                }
            }
        }
        return false;
    }

    public String readableProfile(long time) {
        int i = 0;
        String[] units = new String[]{"ms", "s", "m", "hr", "day", "week", "mnth", "yr"};
        int[] metric = new int[]{1000, 60, 60, 24, 7, 30, 12};
        long current = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);

        for (i = 0; current > metric[i]; i++) {
            current /= metric[i];
        }

        return current + " " + units[i] + ((current > 1 && i > 1) ? "s" : "");
    }

    private void setupDatabase() {

        SQL_Vars.HOST = this.getConfig().getString("mysql.host");
        SQL_Vars.DATABASE = this.getConfig().getString("mysql.database");
        SQL_Vars.USER = this.getConfig().getString("mysql.username");
        SQL_Vars.PASS = this.getConfig().getString("mysql.password");
        SQL_Vars.PORT = this.getConfig().getString("mysql.port");

        db = new MySQL();
        this.getLogger().info("Connecting to MySQL database...");
        try {
            db.open();
            if (db.checkConnection()) {
                this.getLogger().info("Successfully connected to database!");
                if (!db.checkTable("playTime")) {
                    this.getLogger().log(Level.INFO, "Creating table ''playTime'' in database {0}", SQL_Vars.DATABASE);
                    ResultSet result = db.query("CREATE TABLE testing ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    result.close();
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Integer getTime(String username) {
        try {
            Player player = Bukkit.getPlayer(username);

            db = new MySQL();

            db.open();
            if (!db.checkTable("playTime")) {
                return Integer.valueOf(0);
            } else {
                ResultSet result = db.query("SELECT COUNT(*) FROM playTime WHERE username='" + player.getName() + "'");
                result.first();
                if (result.getInt(1) == 0) {
                    result.close();
                    db.close();
                    return Integer.valueOf(0);
                } else {
                    result.close();
                    result = db.query("SELECT playtime FROM playTime WHERE username='" + player.getName() + "'");
                    result.first();
                    int i = Integer.valueOf(result.getInt(1));
                    db.close();
                    return i;
                }
            }
        } catch (SQLException e) {
            if (debug == 3) {
                e.printStackTrace();
            }
        }
        try {
            db.close();
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.valueOf(0);
    }
}