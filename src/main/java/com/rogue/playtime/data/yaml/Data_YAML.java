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
package com.rogue.playtime.data.yaml;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.DataHandler;
import com.rogue.playtime.runnable.StartConvertRunnable;
import com.rogue.playtime.runnable.AddRunnable;
import com.rogue.playtime.runnable.ResetRunnable;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * YAML Data Manager. Check DataHandler for information about each method
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class Data_YAML implements DataHandler {

    private YAML yaml;
    private Playtime plugin;

    public String getName() {
        return "flatfile";
    }

    public int getValue(String data, String username) {
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        return yaml.getFile().getInt("users." + plugin.getBestPlayer(username) + "." + data);
    }

    public Map<String, Integer> getTopPlayers(String data, int amount) {
        return new HashMap<String, Integer>();
    }

    public Map<String, Integer> getPlayersInRange(String timer, int maximum, int minimum) {
        return new HashMap<String, Integer>();
    }

    public void onDeath(String username) {
        plugin.getExecutiveManager().runAsyncTask(new ResetRunnable(plugin, username, "deathtime"), 0L);
    }

    public void onLogout(String username) {
        plugin.getExecutiveManager().runAsyncTask(new ResetRunnable(plugin, username, "onlinetime"), 0L);
    }

    public void verifyFormat() {
        if (plugin.firstRun()) {
            ConfigurationSection section = yaml.getFile().getConfigurationSection("users");
            for (String s : section.getKeys(false)) {
                yaml.getFile().set("users." + s + ".onlinetime", 0);
            }
            yaml.forceSave();
        }
    }

    public void init() {
        yaml = new YAML();
        plugin = Playtime.getPlugin();
    }

    public void startRunnables() {
        plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(plugin), 60L, 60L);
    }

    public void startConversion(String newType, String... players) {
        plugin.onDisable();
        plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(plugin, newType, players), 0L);
    }

    public void cleanup() {
        yaml.forceSave();
        yaml = null;
    }
}
