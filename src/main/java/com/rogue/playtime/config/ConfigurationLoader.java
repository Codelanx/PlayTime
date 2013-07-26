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
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class ConfigurationLoader {
    
    private Playtime plugin;
    private File file = new File(plugin.getDataFolder() + File.separator + "config.yml");
    
    public ConfigurationLoader(Playtime p) {
        plugin = p;
    }
    
    public void verifyConfig() {
        if (!file.exists()) {
            //Add default config from resources
            return;
        }
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        if (!yaml.isSet("debug-level")) {
            yaml.set("debug-level", 0);
        }
        if (!yaml.isSet("update-check")) {
            yaml.set("update-check", true);
        }
        if (!yaml.isSet("check-deaths")) {
            yaml.set("check-deaths", true);
        }
        if (!yaml.isSet("afk.enabled")) {
            yaml.set("afk.enabled", true);
        }
        if (!yaml.isSet("afk.interval")) {
            yaml.set("afk.interval", 60);
        }
        if (!yaml.isSet("afk.timeout")) {
            yaml.set("afk.timeout", 900);
        }
        if (!yaml.isSet("data.manager")) {
            yaml.set("data.manager", "mysql");
        }
        if (!yaml.isSet("managers.mysql.host")) {
            yaml.set("managers.mysql.host", "localhost");
        }
    }

}
