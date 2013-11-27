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
package com.rogue.playtime.update;

import com.rogue.playtime.Playtime;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Handles the update process for {@link Playtime}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class UpdateHandler {

    protected final Playtime plugin;
    private boolean update = false;

    public UpdateHandler(Playtime plugin) {
        this.plugin = plugin;
        this.plugin.getExecutiveManager().runAsyncTask(new UpdateRunnable(this.plugin), 2L);
    }

    protected void setUpdate(boolean value) {
        this.update = value;
    }

    public boolean isUpdateAvailable() {
        return this.update;
    }

    protected void runUpdateListener() {
        this.plugin.getListenerManager().registerListener("update", 
                new UpdateListener(Playtime.__(this.plugin.getCipher().getString("listener.update"))));
    }

}

/**
 * Runs an update check
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
class UpdateRunnable extends UpdateHandler implements Runnable {

    private final String VERSION_URL = "https://raw.github.com/1Rogue/PlayTime/master/VERSION";

    /**
     * Constructor for {@link UpdateRunnable}
     *
     * @since 1.0.0
     * @version 1.0.0
     *
     * @param plugin The {@link InventoryShop} instance
     */
    public UpdateRunnable(Playtime plugin) {
        super(plugin);
    }

    /**
     * Checks for an update
     *
     * @since 1.0.0
     * @version 1.0.0
     */
    public void run() {
        String curVersion = this.plugin.getDescription().getVersion();
        InputStream stream = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            URL call = new URL(VERSION_URL);
            stream = call.openStream();
            isr = new InputStreamReader(stream);
            reader = new BufferedReader(isr);
            String latest = reader.readLine();
            super.setUpdate(!latest.equalsIgnoreCase(curVersion));
            if (super.isUpdateAvailable()) {
                super.runUpdateListener();
            }
        } catch (MalformedURLException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Error checking for an update",
                    this.plugin.getDebug() >= 3 ? ex : "");
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Error checking for an update",
                    this.plugin.getDebug() >= 3 ? ex : "");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE,
                        "Error closing updater streams!",
                        this.plugin.getDebug() >= 3 ? ex : "");
            }
        }
    }

}

/**
 * Actions to take if an update is available
 *
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.4.1
 */
class UpdateListener implements Listener {

    private final String message;

    public UpdateListener(String message) {
        this.message = message;
    }

    /**
     * Sends a notification to ops/players with all of the plugin's permissions
     *
     * @since 1.2.0
     * @version 1.4.1
     *
     * @param e The join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().hasPermission("playtime.updatenotice")) {
            e.getPlayer().sendMessage(this.message);
        }
    }

}
