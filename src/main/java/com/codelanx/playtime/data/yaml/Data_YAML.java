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
package com.codelanx.playtime.data.yaml;

import com.codelanx.playtime.Playtime;
import com.codelanx.playtime.data.DataHandler;
import com.codelanx.playtime.runnable.StartConvertRunnable;
import com.codelanx.playtime.runnable.AddRunnable;
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

    private final Playtime plugin;
    private YAML yaml;
    
    public Data_YAML(Playtime plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return "flatfile";
    }

    public int getValue(String data, String username) {
        if (data.equals("onlinetime") && !Bukkit.getPlayer(username).isOnline()) {
            return -1;
        }
        return this.yaml.getFile().getInt("users." + this.plugin.getBestPlayer(username) + "." + data);
    }

    public Map<String, Integer> getTopPlayers(String data, byte amount) {
        return new HashMap<String, Integer>();
    }

    public Map<String, Integer> getPlayersInRange(String timer, int minimum, int maximum) {
        return new HashMap<String, Integer>();
    }

    public void verifyFormat() {
        if (this.plugin.firstRun()) {
            ConfigurationSection section = this.yaml.getFile().getConfigurationSection("users");
            for (String s : section.getKeys(false)) {
                this.yaml.getFile().set("users." + s + ".onlinetime", 0);
            }
            this.yaml.forceSave();
        }
    }

    public void init() {
        this.yaml = new YAML();
    }

    public void startRunnables() {
        this.plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(this.plugin), 60L, 60L);
    }

    public void startConversion(String newType, String... players) {
        this.plugin.onDisable();
        this.plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(this.plugin, newType, players), 0L);
    }

    public void cleanup() {
        this.yaml.forceSave();
        this.yaml = null;
    }
}
