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

    private final Playtime plugin;
    private final File file;
    private YamlConfiguration yaml = null;

    public ConfigurationLoader(Playtime plugin) {
        this.plugin = plugin;
        this.file = new File(this.plugin.getDataFolder(), "config.yml");
        verifyConfig();
    }

    /**
     * Verifies the values within the configuration, and the file itself
     * 
     * @since 1.3.0
     * @version 1.4.0
     */
    private void verifyConfig() {
        if (this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdir();
        }
        if (!this.file.exists()) {
            this.plugin.saveDefaultConfig();
            this.yaml = YamlConfiguration.loadConfiguration(this.file);
        } else {
            this.yaml = YamlConfiguration.loadConfiguration(this.file);
            if (!this.yaml.isSet("general.debug-level")) { this.yaml.set("general.debug-level", 0); }
            if (!this.yaml.isSet("update.check")) { this.yaml.set("update.check", true); }
            if (!this.yaml.isSet("update.download")) { this.yaml.set("update.download", true); }
            if (!this.yaml.isSet("language.use-github")) { this.yaml.set("language.use-github", true); }
            if (!this.yaml.isSet("language.locale")) { this.yaml.set("language.locale", "en_US"); }
            if (!this.yaml.isSet("check.death-time")) { this.yaml.set("check.death-time", true); }
            if (!this.yaml.isSet("check.online-time")) { this.yaml.set("check.online-time", true); }
            if (!this.yaml.isSet("afk.enabled")) { this.yaml.set("afk.enabled", true); }
            if (!this.yaml.isSet("afk.interval")) { this.yaml.set("afk.interval", 60); }
            if (!this.yaml.isSet("afk.timeout")) { this.yaml.set("afk.timeout", 900); }
            if (!this.yaml.isSet("afk.check-chat")) { this.yaml.set("afk.check-chat", false); }
            if (!this.yaml.isSet("events.enabled")) { this.yaml.set("events.enabled", true); }
            if (!this.yaml.isSet("events.interval")) { this.yaml.set("events.interval", 600); }
            if (!this.yaml.isSet("data.manager")) { this.yaml.set("data.manager", "flatfile"); }
            if (!this.yaml.isSet("managers.mysql.host")) { this.yaml.set("managers.mysql.host", "localhost"); }
            if (!this.yaml.isSet("managers.mysql.port")) { this.yaml.set("managers.mysql.port", "3306"); }
            if (!this.yaml.isSet("managers.mysql.database")) { this.yaml.set("managers.mysql.database", "minecraft"); }
            if (!this.yaml.isSet("managers.mysql.username")) { this.yaml.set("managers.mysql.username", "root"); }
            if (!this.yaml.isSet("managers.mysql.password")) { this.yaml.set("managers.mysql.password", "password"); }

            this.saveConfig();
        }
    }

    /**
     * Saves the current configuration from memory
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public void saveConfig() {
        try {
            this.yaml.save(this.file);
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
        return this.yaml;
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
        return this.yaml.getString(path);
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
        return this.yaml.getInt(path);
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
        return this.yaml.getBoolean(path);
    }
}
