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

import com.rogue.playtime.command.CommandHandler;
import com.rogue.playtime.data.DataManager;
import com.rogue.playtime.listener.PlaytimeListener;
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.metrics.Metrics;
import com.rogue.playtime.player.PlayerHandler;
import com.rogue.playtime.runnable.AFKRunnable;
import com.rogue.playtime.runnable.UpdateRunnable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * @since 1.0
 * @author 1Rogue
 * @version 1.2.0
 */
public class Playtime extends JavaPlugin {
    
    protected BukkitTask afkChecker;
    protected int debug = 0;
    protected PlaytimeListener listener;
    protected PlayerHandler phandler;
    protected CommandHandler chandler;
    protected DataManager dmanager;
    private boolean afkEnabled = true;
    private boolean deathEnabled = true;

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
            this.getConfig().addDefault("debug-level", 0);
            this.getConfig().addDefault("update-check", true);
            this.getConfig().addDefault("check-deaths", true);
            this.getConfig().addDefault("afk.enabled", true);
            this.getConfig().addDefault("afk.interval", 60);
            this.getConfig().addDefault("afk.timeout", 900);
            this.getConfig().addDefault("data.manager", "mysql");
            this.getConfig().addDefault("managers.mysql.host", "localhost");
            this.getConfig().addDefault("managers.mysql.port", "3306");
            this.getConfig().addDefault("managers.mysql.database", "minecraft");
            this.getConfig().addDefault("managers.mysql.username", "root");
            this.getConfig().addDefault("managers.mysql.password", "password");
            this.getConfig().addDefault("managers.sqlite.database", "minecraft");
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
     * @version 1.2.0
     */
    @Override
    public void onEnable() {
        final long startTime = System.nanoTime();

        debug = this.getConfig().getInt("debug-level");
        if (debug >= 1) {
            this.getLogger().log(Level.INFO, "Debug level set to {0}!", debug);
        }

        try {
            Metrics metrics = new Metrics(this);
            getLogger().info("Enabling Metrics...");
            metrics.start();
        } catch (IOException ex) {
            Logger.getLogger(Playtime.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (this.getConfig().getBoolean("update-check")) {
            Bukkit.getScheduler().runTaskLater(this, new UpdateRunnable(this), 1);
        } else {
            this.getLogger().info("Update checks disabled!");
        }

        this.getLogger().log(Level.INFO, "Enabling Data Manager...");
        dmanager = new DataManager(this);
        dmanager.select(this.getConfig().getString("data.manager"));
        dmanager.start();
        
        this.getLogger().info("Enabling Command Handler...");
        chandler = new CommandHandler(this);
        chandler.registerExecs();

        afkEnabled = this.getConfig().getBoolean("afk.enabled");
        deathEnabled = this.getConfig().getBoolean("check-deaths");


        if (afkEnabled) {
            this.getLogger().log(Level.INFO, "Enabling Player Handler...");
            phandler = new PlayerHandler(this, this.getConfig().getInt("afk.interval"), this.getConfig().getInt("afk.timeout"));
            afkChecker = Bukkit.getScheduler().runTaskTimer(this, new AFKRunnable(this), phandler.getAFKCheckInterval() * 20L, phandler.getAFKCheckInterval() * 20L);
        } else {
            this.getLogger().log(Level.INFO, "AFK checking disabled!");
            phandler = null;
        }

        if (!(!afkEnabled && !deathEnabled)) {
            this.getLogger().log(Level.INFO, "Enabling Listener...");
            listener = new PlaytimeListener(this);
            Bukkit.getPluginManager().registerEvents(listener, this);
        } else {
            this.getLogger().log(Level.INFO, "Listener Disabled!");
            listener = null;
        }

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
        if (afkChecker != null) {
            afkChecker.cancel();
        }
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
     * Finds a match for an online player, or offline if there is no good match.
     *
     * @since 1.1
     * @version 1.1
     *
     * @param username The player to look up
     * @return A potential match for a player
     */
    public String getBestPlayer(String username) {
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

    /**
     * Returns an instance of the Playtime Plugin
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return The Playtime plugin instance
     */
    public static Playtime getPlugin() {
        return (Playtime) Bukkit.getPluginManager().getPlugin("Playtime");
    }

    /**
     * Returns Playtime's listener
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return The listener for Playtime
     */
    public PlaytimeListener getListener() {
        return listener;
    }

    /**
     * Returns the player handler for Playtime NOTE: If AFK is disabled, this
     * will return null
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return The plugin's player handler
     */
    public PlayerHandler getPlayerHandler() {
        return phandler;
    }

    /**
     * Returns whether or not AFK is enabled
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return Status of AFK runnable
     */
    public boolean isAFKEnabled() {
        return afkEnabled;
    }

    /**
     * Returns whether or not death timing is enabled.
     *
     * @since 1.2.0
     * @version 1.2.0
     *
     * @return Status of Death checking
     */
    public boolean isDeathEnabled() {
        return deathEnabled;
    }
    
    /**
     * Converts pre-made strings to have chat colors in them
     * 
     * @param encoded String with unconverted color codes
     * @return string with correct chat colors included
     */
    public static String _(String encoded) {
        return ChatColor.translateAlternateColorCodes('&', encoded);
    }
    
    public DataManager getDataManager() {
        return dmanager;
    }
}