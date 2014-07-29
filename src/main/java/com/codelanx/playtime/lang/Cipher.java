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
package com.codelanx.playtime.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * TODO: allow support for multiple languages at once
 *
 * @version 1.4.3
 * @author 1Rogue
 * @since 1.4.0
 */
public class Cipher {

    private final Plugin plugin;
    private final String langFileLocGithub = "https://raw.github.com/1Rogue/Playtime/master/lang/<version>/<lang>.yml";
    private final String langFileLocJar = "<lang>.yml";
    private final String langFileLocFolder = "<plugin>" + File.separatorChar + "lang" + File.separatorChar + "<lang>.yml";
    private final String language;
    private YamlConfiguration langFile;

    /**
     * Cipher Constructor. Loads files from either a local folder, github, or
     * the jarfile, in that listed priority.
     * 
     * @since 1.4.0
     * @version 1.4.3
     *
     * @param plugin The main plugin instance
     * @param langType The name of the lang file to use
     * @param useGit Whether or not downloading from github is allowed
     */
    public Cipher(Plugin plugin, String langType, boolean useGit) {
        this.plugin = plugin;
        this.language = langType;

        if (this.language.equalsIgnoreCase("custom")) {
            YamlConfiguration file = this.getFromFolder(plugin, this.language);
            if (file != null) {
                this.langFile = file;
                return;
            }
        }

        YamlConfiguration github = null;
        if (useGit) {
            try {
                github = getFromGithub(plugin, this.language);
            } catch (FileNotFoundException e) {
                plugin.getLogger().log(Level.SEVERE, "Cannot find lang file {0}!", this.language);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Fatal error occured while retrieving lang files", e);
            }
        }
        
        try {
            YamlConfiguration file = this.getFromFolder(plugin, this.language);
            if (file != null) {
                int version = file.getInt("version", 0);
                int gitVersion = version;
                if (github != null) {
                    gitVersion = github.getInt("version", version);
                }
                if (gitVersion > version) {
                    plugin.getLogger().warning("Your language file is outdated, getting new file");
                    file = github;
                } else {
                    github = null;
                }
            } else {
                file = this.getFromJar(plugin, this.language);
                if (file == null) {
                    file = github;
                    if (file == null) {
                        throw new InvalidConfigurationException("The langauage " + this.language + " is unsupported");
                    }
                }
            }
            this.langFile = file;
        } catch (Exception e) {
            //and if we just completely crash and burn, then use en_US
            plugin.getLogger().log(Level.SEVERE, "Fatal error occured while loading lang files:", e);
            plugin.getLogger().log(Level.SEVERE, "Defaulting to english (en_US)");
            this.langFile = getFromJar(plugin, this.language);
        }
        
        try {
            File f = new File(this.langFileLocFolder.replace("<plugin>",
                    plugin.getDataFolder().getPath()).replace("<lang>", this.language));
            f.getParentFile().mkdirs();
            this.langFile.save(f);
        } catch (IOException ex) {
            Logger.getLogger(Cipher.class.getName()).log(Level.SEVERE, "Fatal error occured while saving lang files", ex);
        }
        
    }

    /**
     * Gets the message for this key in the used language. If the key does not
     * exist, this will default to use the en_US in the jarfile.
     *
     * @since 1.4.0
     * @version 1.4.3
     *
     * @param path The path to the string
     * @param vars Any variables to add
     * @return The resulting String
     */
    public String getString(String path, Object... vars) {
        if (path == null) {
            throw new NullPointerException("YamlConfiguration string path cannot be null!");
        }
        
        String back = this.langFile.getString(path);
        if (back == null) {
            back = "&cUnable to find path &6" + path + " &cin lang file &6" + this.language;
        }
        for (byte i = 0; i < vars.length; i++) {
            back = back.replace("{" + i + "}", vars[i].toString());
        }
        return ChatColor.translateAlternateColorCodes('&', back);
    }

    /**
     * Gets the lang file from the plugin data folder
     *
     * @since 1.4.0
     * @version 1.4.3
     *
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a YamlConfiguration
     */
    private YamlConfiguration getFromFolder(Plugin pl, String lang) {
        File file = new File(this.langFileLocFolder.replace("<plugin>", pl.getDataFolder().getPath()).replace("<lang>", lang));
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
     * @version 1.4.3
     *
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a YamlConfiguration
     */
    private YamlConfiguration getFromJar(Plugin plugin, String lang) {
        InputStream jarStream = plugin.getResource(this.langFileLocJar.replace("<lang>", lang));
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
     * @version 1.4.3
     *
     * @param pl The plugin
     * @param lang The lang file to use
     * @return The lang file as a YamlConfiguration
     */
    private YamlConfiguration getFromGithub(Plugin plugin, String lang) throws MalformedURLException, IOException {
        YamlConfiguration pluginyml = YamlConfiguration.loadConfiguration(plugin.getResource("plugin.yml"));

        URL upstr = new URL(this.langFileLocGithub.replace("<version>", pluginyml.getString("version")).replace("<lang>", lang));
        InputStream langs = upstr.openStream();
        if (langs != null) {
            return YamlConfiguration.loadConfiguration(langs);
        } else {
            return null;
        }
    }
}
