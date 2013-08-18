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
package com.rogue.playtime.config;

import com.rogue.playtime.Playtime;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Manages the configuration for the plugin
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ConfigurationLoader {

    private Playtime plugin;
    private YamlConfiguration yaml = null;
    private File file;

    public ConfigurationLoader(Playtime p) {
        plugin = p;
        file = new File(plugin.getDataFolder(), "config.yml");
        verifyConfig();
    }

    /**
     * Verifies the values within the configuration, and the file itself
     * 
     * @since 1.3.0
     * @version 1.4.0
     */
    public void verifyConfig() {
        if (plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!file.exists()) {
            plugin.saveDefaultConfig();
            return;
        }
        yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.isSet("general.debug-level")) { yaml.set("general.debug-level", 0); }
        if (!yaml.isSet("general.update-check")) { yaml.set("general.update-check", true); }
        if (!yaml.isSet("language.use-github")) { yaml.set("language.use-github", true); }
        if (!yaml.isSet("language.locale")) { yaml.set("language.locale", "en_US"); }
        if (!yaml.isSet("check.death-time")) { yaml.set("check.death-time", true); }
        if (!yaml.isSet("check.online-time")) { yaml.set("check.online-time", true); }
        if (!yaml.isSet("afk.enabled")) { yaml.set("afk.enabled", true); }
        if (!yaml.isSet("afk.interval")) { yaml.set("afk.interval", 60); }
        if (!yaml.isSet("afk.timeout")) { yaml.set("afk.timeout", 900); }
        if (!yaml.isSet("afk.check-chat")) { yaml.set("afk.check-chat", false); }
        if (!yaml.isSet("events.enabled")) { yaml.set("events.enabled", true); }
        if (!yaml.isSet("events.interval")) { yaml.set("events.interval", 600); }
        if (!yaml.isSet("data.manager")) { yaml.set("data.manager", "flatfile"); }
        if (!yaml.isSet("managers.mysql.host")) { yaml.set("managers.mysql.host", "localhost"); }
        if (!yaml.isSet("managers.mysql.port")) { yaml.set("managers.mysql.port", "3306"); }
        if (!yaml.isSet("managers.mysql.database")) { yaml.set("managers.mysql.database", "minecraft"); }
        if (!yaml.isSet("managers.mysql.username")) { yaml.set("managers.mysql.username", "root"); }
        if (!yaml.isSet("managers.mysql.password")) { yaml.set("managers.mysql.password", "password"); }

        this.saveConfig();
    }

    /**
     * Saves the current configuration from memory
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public void saveConfig() {
        try {
            yaml.save(file);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationLoader.class.getName()).log(Level.SEVERE, "Error saving configuration file!", ex);
        }
    }

    /**
     * Gets the configuration file for Playtime
     *
     * @since 1.3.0
     * @version 1.3.0
     *
     * @return YamlConfiguration file, null if verifyConfig() has not been run
     */
    public YamlConfiguration getConfig() {
        return yaml;
    }
    
    /**
     * Gets a string value from the config
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param path Path to string value
     * @return String value
     */
    public synchronized String getString(String path) {
        return yaml.getString(path);
    }
    
    /**
     * Gets an int value from the config
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param path Path to int value
     * @return int value
     */
    public synchronized int getInt(String path) {
        return yaml.getInt(path);
    }
    
    /**
     * Gets a boolean value from the config
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param path Path to boolean value
     * @return boolean value
     */
    public synchronized boolean getBoolean(String path) {
        return yaml.getBoolean(path);
    }
}
