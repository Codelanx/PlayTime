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

import com.rogue.playtime.listener.PlaytimeListener;
import com.rogue.playtime.runnable.AddRunnable;
import com.rogue.playtime.sql.SQL_Vars;
import com.rogue.playtime.sql.db.MySQL;
import com.rogue.playtime.metrics.Metrics;
import com.rogue.playtime.runnable.UpdateRunnable;
import java.io.File;
import java.io.IOException;
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

/**
 * @since 1.0
 * @author 1Rogue
 * @version 1.2
 */
public class Playtime extends JavaPlugin {

    public MySQL db;
    protected BukkitTask updater;
    protected int debug = 0;
    protected PlaytimeListener listener;
    
    /**
     * Registers the plugin configuration file.
     * 
     * @since 1.0
     * @version 1.1
     */
    @Override
    public void onLoad() {
        File file = new File(getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {
            this.getLogger().info("Generating first time config.yml...");
            this.getConfig().addDefault("debug-level", "0");
            this.getConfig().addDefault("update-check", true);
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
    }
    
    /**
     * Registers runnables and the listener on plugin start, as well as the
     * plugin data storage.
     * 
     * @since 1.0
     * @version 1.1
     */
    @Override
    public void onEnable() {
        final long startTime = System.nanoTime();

        try {
            Metrics metrics = new Metrics(this);
            getLogger().info("Enabling Metrics...");
            metrics.start();
        } catch (IOException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.getConfig().getBoolean("update-check")) {
            Bukkit.getScheduler().runTaskLater(this, new UpdateRunnable(this), 1);
        }
        
        this.getLogger().log(Level.INFO, "Validating Database...");
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
        
        this.getLogger().log(Level.INFO, "Enabling Listener...");
        listener = new PlaytimeListener(this);
        Bukkit.getPluginManager().registerEvents(listener, this);

        final long endTime = System.nanoTime();
        if (debug >= 1) {
            final long duration = endTime - startTime;
            this.getLogger().log(Level.INFO, "Enabled ({0})", this.readableProfile(duration));
        }
    }
    
    /**
     * Closes tasks and sql connections on plugin disabling.
     * 
     * @since 1.0
     * @version 1.1
     */
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
    
    /**
     * Command Executor
     * 
     * @since 1.0
     * @version 1.2
     * 
     * @deprecated
     * 
     * @param sender The command executor
     * @param cmd The command object
     * @param commandLabel The name of the command
     * @param args Command Arguments
     * @return Success of command
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("playtime")) {
            String check;
            String perm = "playtime.use";
            if (args.length == 0 && sender instanceof Player) {
                check = sender.getName();
            } else if (args.length == 1) {
                check = this.getBestPlayer(args[0]);
                perm += ".others";
            } else {
                sender.sendMessage("You cannot check the playtime of a non-player!");
                return true;
            }
            if (sender.hasPermission(perm)) {
                int time = this.getValue("playtime", check);
                int minutes = time % 60;
                if (time >= 60) {
                    int hours = time / 60;
                    sender.sendMessage(ChatColor.GOLD + check + " has played for " + hours + " hour" + (hours == 1 ? "" : "s") + " and " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                } else {
                    sender.sendMessage(ChatColor.GOLD + check + " has played for " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                }
            } else {
                sender.sendMessage("[" + ChatColor.YELLOW + "PlayTime" + ChatColor.RESET + "] " + ChatColor.GOLD + "You do not have permission to do that!");
            }
            return false;
        } else if (cmd.getName().equalsIgnoreCase("deathtime")) {
            String check;
            String perm = "playtime.death";
            if (args.length == 0 && sender instanceof Player) {
                check = sender.getName();
            } else if (args.length == 1) {
                check = this.getBestPlayer(args[0]);
                perm += ".others";
            } else {
                sender.sendMessage("You cannot check the survival time of a non-player!");
                return true;
            }
            if (sender.hasPermission(perm)) {
                int time = this.getValue("deathtime", check);
                int minutes = time % 60;
                if (time >= 60) {
                    int hours = time / 60;
                    sender.sendMessage(ChatColor.GOLD + check + " has been alive for " + hours + " hour" + (hours == 1 ? "" : "s") + " and " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                } else {
                    sender.sendMessage(ChatColor.GOLD + check + " has been alive for " + minutes + " minute" + (minutes == 1 ? "" : "s") + ".");
                }
            } else {
                sender.sendMessage("[" + ChatColor.YELLOW + "PlayTime" + ChatColor.RESET + "] " + ChatColor.GOLD + "You do not have permission to do that!");
            }
            return false;
        }
        return false;
    }
    
    /**
     * Makes a long-type time value into a readable string.
     * 
     * @since 1.0
     * @version 1.0
     * 
     * @param time The time value as a long
     * @return Readable String of the time
     */
    public String readableProfile(long time) {
        int i;
        String[] units = new String[]{"ms", "s", "m", "hr", "day", "week", "mnth", "yr"};
        int[] metric = new int[]{1000, 60, 60, 24, 7, 30, 12};
        long current = TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS);

        for (i = 0; current > metric[i]; i++) {
            current /= metric[i];
        }

        return current + " " + units[i] + ((current > 1 && i > 1) ? "s" : "");
    }
    
    /**
     * Sets up the SQL data from the config values, and validates tables.
     * 
     * @since 1.0
     * @version 1.1
     */
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
                    ResultSet result = db.query("CREATE TABLE playTime ( id int NOT NULL AUTO_INCREMENT, username VARCHAR(32) NOT NULL, playtime int NOT NULL, deathtime int NOT NULL, PRIMARY KEY (id), UNIQUE KEY (username)) ENtestingGINE=MyISAM;");
                    result.close();
                } else {
                    try {
                        db.update("ALTER TABLE `playTime` ADD deathtime int NOT NULL");
                        this.getLogger().info("Updating SQL table for 1.2");
                    } catch (SQLException e){
                        this.getLogger().info("SQL table is up to date!");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets the value of a column for a particular player.
     * 
     * @since 1.2.0
     * @version 1.2.0
     * 
     * @param username The player to look up
     * @return The value of the provided column, 0 if no value found.
     */
    private int getValue(String value, String username) {
        int ret = 0;
        try {
            db = new MySQL();
            db.open();
            ResultSet result = db.query("SELECT `" + value + "` FROM `playTime` WHERE `username`='" + username + "'");
            result.first();
            ret = result.getInt(1);
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
        return ret;
    }
    
    /**
     * Finds a match for an online player, or offline if there is no good match.
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @param username The player to look up
     * @return A potential match for a player
     */
    private String getBestPlayer(String username) {
        Player player = Bukkit.getPlayer(username);
        if (player != null) {
            username = player.getName();
        }
        return username;
    }
    
    /**
     * Gets the level of debugging for players
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @return The debug level
     */
    public int getDebug() {
        return debug;
    }
    
    public static Playtime getPlugin() {
        return (Playtime)Bukkit.getPluginManager().getPlugin("Playtime");
    }
    
    public PlaytimeListener getListener() {
        return listener;
    }
}