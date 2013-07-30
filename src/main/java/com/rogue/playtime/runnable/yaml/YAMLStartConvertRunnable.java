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

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.yaml.YAML;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class YAMLStartConvertRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String convertTo;
    private final String[] player;

    public YAMLStartConvertRunnable(Playtime p, String newType, String... players) {
        plugin = p;
        convertTo = newType;
        player = players;
    }

    public void run() {
        YAML yaml = new YAML();

        ConfigurationSection users = yaml.getFile().getConfigurationSection("users");
        int i = 1;
        StringBuilder sb;
        String out = "null";
        if (convertTo.equals("mysql")) {
            sb = new StringBuilder("INSERT INTO `playTime` (`username`, `playtime`, `deathtime`, `onlinetime`) VALUES ");
            for (String s : users.getKeys(false)) {
                sb.append("('").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append("), ");
            }
            out = sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`";

        } else if (convertTo.equals("sqlite")) {
            sb = new StringBuilder("INSERT INTO `playTime` ");
            for (String s : users.getKeys(false)) {
                if (i > 1) {
                    sb.append("UNION SELECT ").append(i).append(", '").append(s).append("', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(", ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append(" ");
                } else {
                    sb.append("SELECT ").append(i).append(" AS 'column1', '").append(s).append("' AS 'column2', ").append(yaml.getFile().getInt("users." + s + ".playtime")).append(" AS 'column3', ").append(yaml.getFile().getInt("users." + s + ".deathtime")).append(" AS 'column4', ").append(yaml.getFile().getInt("users." + s + ".onlinetime")).append(" AS 'column5' ");
                }
                i++;
            }
            out = sb.substring(0, sb.length() - 1);
        }
        if (!out.equals("null")) {
            plugin.getDataManager().convertTo(convertTo, out, player);
        }
    }
}
