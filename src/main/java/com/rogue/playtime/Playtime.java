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
import com.rogue.playtime.config.ConfigurationLoader;
import com.rogue.playtime.data.DataManager;
import com.rogue.playtime.event.EventHandler;
import com.rogue.playtime.listener.PlaytimeListener;
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
 * The main class
 * 
 * @since 1.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class Playtime extends JavaPlugin {
    
    protected BukkitTask afkChecker;
    protected int debug = 0;
    protected PlaytimeListener listener;
    protected PlayerHandler phandler;
    protected CommandHandler chandler;
    protected DataManager dmanager;
    protected ConfigurationLoader cloader;
    protected EventHandler ehandler;
    private boolean deathEnabled = true;
    private boolean onlineEnabled = true;
    private boolean isUpdate = false;
    private boolean isBusy = false;

    /**
     * Registers the plugin configuration file.
     *
     * @since 1.0
     * @version 1.3.0
     */
    @Override
    public void onLoad() {
        File file = new File(getDataFolder() + File.separator + "config.yml");

        this.getLogger().info("Loading Configuration mananger...");
        cloader = new ConfigurationLoader(this);
        cloader.verifyConfig();
    }

    /**
     * Registers debug, metrics, commands, data management, and listeners.
     *
     * @since 1.0
     * @version 1.3.0
     */
    @Override
    public void onEnable() {
        final long startTime = System.nanoTime();
        
        debug = cloader.getInt("debug-level");
        if (debug > 3) {
            debug = 3;
        }
        if (debug < 0) {
            debug = 0;
        }
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

        if (cloader.getBoolean("update-check")) {
            Bukkit.getScheduler().runTaskLater(this, new UpdateRunnable(this), 1);
        } else {
            this.getLogger().info("Update checks disabled!");
        }

        this.getLogger().log(Level.INFO, "Enabling Data Manager...");
        dmanager = new DataManager(this);
        dmanager.select(cloader.getString("data.manager"));
        dmanager.setup();
        dmanager.start();
        
        this.getLogger().info("Enabling Command Handler...");
        chandler = new CommandHandler(this);
        chandler.registerExecs();

        boolean afkEnabled = this.cloader.getBoolean("afk.enabled");
        deathEnabled = this.cloader.getBoolean("check.death-time");
        onlineEnabled = this.cloader.getBoolean("check.online-time");


        if (afkEnabled) {
            this.getLogger().log(Level.INFO, "Enabling Player Handler...");
            phandler = new PlayerHandler(this, cloader.getInt("afk.interval"), cloader.getInt("afk.timeout"));
            afkChecker = Bukkit.getScheduler().runTaskTimer(this, new AFKRunnable(this), phandler.getAFKCheckInterval() * 20L, phandler.getAFKCheckInterval() * 20L);
        } else {
            this.getLogger().log(Level.INFO, "AFK checking disabled!");
            phandler = null;
        }

        if (!(!afkEnabled && !deathEnabled && !onlineEnabled && !cloader.getBoolean("update-check"))) {
            this.getLogger().log(Level.INFO, "Enabling Listener...");
            listener = new PlaytimeListener(this);
            Bukkit.getPluginManager().registerEvents(listener, this);
        } else {
            this.getLogger().log(Level.INFO, "Listener Disabled!");
            listener = null;
        }
        
        if (cloader.getBoolean("events.enabled")) {
            this.getLogger().log(Level.INFO, "Enabling event system...");
            ehandler = new EventHandler(this);
        } else {
            this.getLogger().log(Level.INFO, "Disabling event system!");
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
     * @version 1.3.0
     */
    @Override
    public void onDisable() {
        dmanager.getDataHandler().cleanup();
        ehandler.cancelChecks();
        if (afkChecker != null) {
            afkChecker.cancel();
        }
        afkChecker = null;
        this.getServer().getScheduler().cancelTasks(this);
    }
    
    /**
     * Reloads the plugin
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param names Players to notify when the reload is complete
     */
    public void reload(String... names) {
        this.onDisable();
        afkChecker = null;
        debug = 0;
        listener = null;
        phandler = null;
        chandler = null;
        dmanager = null;
        cloader = null;
        deathEnabled = true;
        onlineEnabled = true;
        isUpdate = false;
        this.onLoad();
        this.onEnable();
        
        this.getLogger().info("Playtime reloaded!");
        for (String s : names) {
            this.getServer().getPlayer(s).sendMessage(_("[&ePlayTime&f] &6Playtime Reloaded!"));
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
        return phandler != null;
    }

    /**
     * Returns whether or not death timing is enabled
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
     * Returns whether or not online timing is enabled
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @return Status of online tracking
     */
    public boolean isOnlineEnabled() {
        return onlineEnabled;
    }
    
    /**
     * Returns the status of the update check
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @return true if update, false if none or no check made.
     */
    public boolean isUpdateAvailable() {
        return isUpdate;
    }
    
    /**
     * Sets whether or not an update is available. Should only be called by
     * the update task.
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param status true if latest version, otherwise false
     * @return The updated value
     */
    public boolean setUpdateStatus(boolean status) {
        isUpdate = status;
        return isUpdate;
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
    
    /**
     * Returns Playtime's abstract data manager
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @return abstract data manager for Playtime
     */
    public DataManager getDataManager() {
        return dmanager;
    }
    
    /**
     * Gets the configuration manager for Playtime
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @return The main ConfigurationLoader
     */
    public ConfigurationLoader getConfigurationLoader() {
        return cloader;
    }
    
    public EventHandler getEventHandler() {
        return ehandler;
    }
    
    public boolean isBusy() {
        return isBusy;
    }
    
    public boolean setBusy(boolean busy) {
        isBusy = busy;
        return isBusy;
    }
}