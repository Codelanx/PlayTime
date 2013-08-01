/*
 * Copyright (C) 2013 AE97
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
package com.rogue.playtime.lang;

import com.rogue.playtime.Playtime;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * Adapted from TotalPermissions
 * 
 * @version 1.4.0
 * @author 1Rogue
 * @since 1.4.0
 */
public class Cipher {

    private FileConfiguration langFile;
    private final String langFileLocGithub = "https://raw.github.com/1Rogue/Playtime/master/lang/<version>/<lang>.yml";
    private final String langFileLocJar = "<lang>.yml";
    private final String langFileLocFolder = "<plugin>" + File.separatorChar + "lang" + File.separatorChar + "<lang>.yml";
    private final String language;

    public Cipher(Playtime plugin) {
        language = plugin.getConfigurationLoader().getString("general.language");
        //load file from github in preps for future use
        if (language.equalsIgnoreCase("custom")) {
            FileConfiguration file = this.getFromFolder(plugin, language);
            if (file != null) {
                setLangFile(file);
                return;
            }
        }
        FileConfiguration github = null;
        try {
            github = getFromGithub(plugin, language);
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot find lang file {0}!", language);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Fatal error occured while retrieving lang files", e);
        }
        try {
            //first see if there is a lang file
            FileConfiguration file = this.getFromFolder(plugin, language);
            if (file != null) {
                int version = file.getInt("version", 0);
                int gitVersion = version;
                if (github != null) {
                    gitVersion = github.getInt("version", version);
                }
                if (gitVersion > version) {
                    plugin.getLogger().warning("Your language file is outdated, getting new file");
                    file = github;
                }
            } else {
                file = this.getFromJar(plugin, language);
                if (file == null) {
                    file = github;
                    if (file == null) {
                        throw new InvalidConfigurationException("The langauage " + language + " is unsupported");
                    }
                }
            }
            setLangFile(file);
        } catch (Exception e) {
            //and if we just completely crash and burn, then use en_US
            plugin.getLogger().log(Level.SEVERE, "Fatal error occured while loading lang files:", e);
            plugin.getLogger().log(Level.SEVERE, "Defaulting to english (en_US)");
            setLangFile(getFromJar(plugin, language));
        }
        try {
            new File(langFileLocFolder.replace("<plugin>", plugin.getDataFolder().getPath())).getParentFile().mkdirs();
            langFile.save(langFileLocFolder.replace("<plugin>", plugin.getDataFolder().getPath()).replace("<lang>", language));
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Fatal error occured while saving lang files", ex);
        }
    }

    private void setLangFile(FileConfiguration file) {
        langFile = file;
    }

    /**
     * Gets the message for this key in the used language. If the key does not
     * exist, this will default to use the en_US in the jarfile.
     * 
     * @since 1.4.0
     * @version 1.4.0
     *
     * @param path The path to the string
     * @param vars Any variables to add
     * @return The resulting String
     */
    public String getString(String path, Object... vars) {
        String string = langFile.getString(path);
        if (string == null) {
            FileConfiguration fromJar = getFromJar(Playtime.getPlugin(), this.langFileLocJar.replace("<lang>", "en_US"));
            if (fromJar != null) {
                string = fromJar.getString(path);
            }
        }
        if (string == null) {
            throw new NullPointerException("The language files are missing the path. Language: " + language + " Path: " + path);
        }
        for (int i = 0; i < vars.length; i++) {
            string = string.replace("{" + i + "}", vars[i].toString());
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    /**
     * Gets the lang file from the plugin data folder
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a FileConfiguration
     */
    private FileConfiguration getFromFolder(Plugin pl, String lang) {
        File file = new File(langFileLocFolder.replace("<plugin>", pl.getDataFolder().getPath()).replace("<lang>", lang));
        if (file.exists()) {
            return YamlConfiguration.loadConfiguration(file);
        } else {
            return null;
        }
    }
    
    /**
     * Gets the lang file from the plugin jarfile
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a FileConfiguration
     */
    private FileConfiguration getFromJar(Plugin plugin, String lang) {
        InputStream jarStream = plugin.getResource(langFileLocJar.replace("<lang>", lang));
        if (jarStream != null) {
            return YamlConfiguration.loadConfiguration(jarStream);
        } else {
            return null;
        }
    }
    
    /**
     * Gets the lang file from github
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a FileConfiguration
     */
    private FileConfiguration getFromGithub(Plugin plugin, String lang) throws MalformedURLException, IOException {
        YamlConfiguration pluginyml = YamlConfiguration.loadConfiguration(plugin.getResource("plugin.yml"));

        URL upstr = new URL(langFileLocGithub.replace("<version>", pluginyml.getString("version")).replace("<lang>", lang));
        InputStream langs = upstr.openStream();
        if (langs != null) {
            return YamlConfiguration.loadConfiguration(langs);
        } else {
            return null;
        }
    }
}
