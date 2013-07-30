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
package com.rogue.playtime.data;

import static com.rogue.playtime.Playtime._;
import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.mysql.Data_MySQL;
import com.rogue.playtime.data.sqlite.Data_SQLite;
import com.rogue.playtime.data.yaml.Data_YAML;
import com.rogue.playtime.runnable.mysql.MySQLConvertToRunnable;
import com.rogue.playtime.runnable.sqlite.SQLiteConvertToRunnable;
import java.util.Map;
import org.bukkit.Bukkit;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class DataManager {
    
    protected final Playtime plugin;
    protected DataHandler data;
    
    public DataManager(Playtime p) {
        plugin = p;
    }
    
    /**
     * Selects the proper data manager to use, based on the configuration set
     * by the user. By default, it will use flatfile if the user enters
     * something that isn't compatible.
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param type The type of data manager to use
     */
    public void select(String type) {
        type = type.toLowerCase();
        if (type.equals("mysql")) {
            data = new Data_MySQL();
        } else if (type.equals("sqlite")) {
            data = new Data_SQLite();
        } else {
            data = new Data_YAML();
        }
    }
    
    /**
     * Runs the startup process for the data manager at hand.
     * 
     * @since 1.3.0
     * @version 1.3.0
     */
    public void setup() {
        data.setup();
        data.verifyFormat();
    }
    
    /**
     * Starts the data updating process
     * 
     * @since 1.4.0
     * @version 1.4.0
     */
    public void start() {
        data.initiateRunnable();
    }
    
    public DataHandler getDataHandler() {
        return data;
    }
    
    public void convertData(String newType, String... players) {
        if (!data.getName().equals(newType)) {
            data.startConversion(newType, players);
        } else {
            for (String s : players) {
                Bukkit.getPlayer(s).sendMessage(_("[&ePlaytime&f] &6Data manager already being used!"));
            }
            plugin.setBusy(false);
        }
    }
    
    public void convertTo(String newType, String query, String... players) {
        if (newType.equals("mysql")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new MySQLConvertToRunnable(plugin, query, players));
        } else if (newType.equals("sqlite")) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, new SQLiteConvertToRunnable(plugin, query, players));
        }
    }

}
