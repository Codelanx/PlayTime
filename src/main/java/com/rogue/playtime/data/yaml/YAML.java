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
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class YAML {
    
    private YamlConfiguration yaml;
    private final String path;
    
    public YAML() {
        path = plugin.getDataFolder() + File.separator + "users.yml";
        File users = new File(path);
        try {
            yaml = this.makeYAML(users);
        } catch (IOException ex) {
            Logger.getLogger(YAML.class.getName()).log(Level.SEVERE, "Error creating user YAML file!", ex);
        }
    }
    
    Playtime plugin = Playtime.getPlugin();
    
    public void incrementValue(String key) {
        int i = yaml.getInt(key);
        yaml.set(key, ++i);
    }
    
    public YamlConfiguration getFile() {
        return yaml;
    }
    
    public void forceSave() {
        try {
            yaml.save(path);
        } catch (IOException ex) {
            Logger.getLogger(YAML.class.getName()).log(Level.SEVERE, "Error saving user yaml configuration!", ex);
        }
    }

    private YamlConfiguration makeYAML(File yaml) throws IOException {
        if (yaml.exists()) {
            return YamlConfiguration.loadConfiguration(yaml);
        }
        yaml.createNewFile();
        return YamlConfiguration.loadConfiguration(yaml);
    }

}
