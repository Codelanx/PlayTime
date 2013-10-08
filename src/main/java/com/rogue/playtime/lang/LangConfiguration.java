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
package com.rogue.playtime.lang;

import com.rogue.playtime.Playtime;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @since
 * @author 1Rogue
 * @version
 */
public class LangConfiguration extends YamlConfiguration {
    
    protected final Playtime plugin;
    protected final String filename;
    
    public LangConfiguration(Playtime plugin, String filename) {
        super();
        this.plugin = plugin;
        this.filename = filename;
    }
    
    @Override
    public String getString(String path) {
        if (path == null) {
            throw new NullPointerException("LangConfiguration string path cannot be null!");
        }
        
        String back = super.getString(path);
        if (back == null) {
            back = "&cUnable to find path &6" + path + " &cin lang file &6" + this.filename;
        }
        return ChatColor.translateAlternateColorCodes('&', back);
    }

}
