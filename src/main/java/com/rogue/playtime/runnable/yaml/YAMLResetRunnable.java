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
package com.rogue.playtime.runnable.yaml;

import com.rogue.playtime.data.yaml.YAML;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class YAMLResetRunnable extends BukkitRunnable {
    
    private final String user;
    private final String value;
    private YAML yaml;

    public YAMLResetRunnable(String u, String feild, YAML y) {
        user = u;
        yaml = y;
        value = feild;
    }

    public void run() {
        yaml.getFile().set("users." + user + "." + value, 0);
        yaml.forceSave();
    }

}
