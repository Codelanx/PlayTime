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
package com.rogue.playtime.runnable;

import com.rogue.playtime.Playtime;
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.data.sqlite.SQLite;
import com.rogue.playtime.data.yaml.YAML;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.2
 */
public class AddRunnable implements Runnable {

    private final Playtime plugin;
    private final boolean afkEnabled;
    private ArrayList<String> timers;

    public AddRunnable(Playtime p) {
        plugin = p;
        afkEnabled = plugin.getPlayerHandler() != null;
        timers = new ArrayList();
        timers.add("playtime");
        if (plugin.getConfigurationLoader().getBoolean("check.death-time")) {
            timers.add("deathtime");
        }
        if (plugin.getConfigurationLoader().getBoolean("check.online-time")) {
            timers.add("onlinetime");
        }
    }

    public void run() {
        String current = plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            Player[] players = plugin.getServer().getOnlinePlayers();
            MySQL db = new MySQL();
            boolean complete = false;
            StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`) VALUES ");
            if (players.length > 0) {
                for (Player p : players) {
                    if (!afkEnabled || !plugin.getPlayerHandler().isAFK(p.getName())) {
                        sb.append("('").append(p.getName()).append("'), ");
                    }
                }
                if (sb.toString().endsWith(" VALUES ")) {
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info(plugin.getCipher().getString("runnable.add.none"));
                    }
                    return;
                }
                sb = new StringBuilder(sb.substring(0, sb.length() - 2));
                sb.append(" ON DUPLICATE KEY UPDATE ");
                for (String timer : timers) {
                    sb.append("`").append(timer).append("`=`").append(timer).append("`+1, ");
                }
                sb = new StringBuilder(sb.substring(0, sb.length() - 2));
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info(plugin.getCipher().getString("runnable.add.update"));
                    if (plugin.getDebug() >= 2) {
                        plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.query", sb.toString()));
                    }
                }
                complete = true;
            }
            try {
                db.open();
                if (db.checkConnection() && complete) {
                    db.update(sb.toString());
                }
                db.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, null);
                if (plugin.getDebug() == 3) {
                    ex.printStackTrace();
                }
            }
        } else if (current.equals("sqlite")) {
            Player[] players = plugin.getServer().getOnlinePlayers();
            if (players.length > 0) {
                StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO `playTime` ");
                StringBuilder sb2 = new StringBuilder("UPDATE `playTime` SET ");
                for (String timer : timers) {
                    sb2.append("`").append(timer).append("`=`").append(timer).append("`+1, ");
                }
                sb2 = new StringBuilder(sb2.substring(0, sb2.length() - 2));
                sb2.append(" WHERE `username` IN (");
                int i = 0;
                for (Player p : players) {
                    if (!afkEnabled || !plugin.getPlayerHandler().isAFK(p.getName())) {
                        sb2.append("'").append(p.getName()).append("', ");
                        if (i > 0) {
                            sb.append("UNION SELECT NULL, '").append(p.getName()).append("', 0, 0, 0 ");
                        } else {
                            sb.append("SELECT NULL AS 'column1', '").append(p.getName()).append("' AS 'column2', 0 AS 'column3', 0 AS 'column4', 0 AS 'column5' ");
                            i++;
                        }
                    }
                }
                if (sb.toString().endsWith(" `playTime` ")) {
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info(plugin.getCipher().getString("runnable.add.none"));
                    }
                    return;
                }
                if (plugin.getDebug() >= 2) {
                    plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.query", sb.substring(0, sb.length() - 1)));
                    plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.query", sb2.substring(0, sb2.length() - 2) + ")"));
                }
                SQLite db = new SQLite();
                try {
                    db.open();

                    db.update(sb.substring(0, sb.length() - 1));
                    db.update(sb2.substring(0, sb2.length() - 2) + ")");

                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info(plugin.getCipher().getString("runnable.add.update"));
                    }
                    db.close();
                } catch (SQLException ex) {
                    plugin.getLogger().log(Level.SEVERE, null);
                    if (plugin.getDebug() == 3) {
                        ex.printStackTrace();
                    }
                }
            }
        } else if (current.equals("flatfile")) {
            YAML yaml = new YAML();
            Player[] players = plugin.getServer().getOnlinePlayers();
            for (Player p : players) {
                if (!afkEnabled || !plugin.getPlayerHandler().isAFK(p.getName())) {
                    for (String timer : timers) {
                        yaml.incrementValue("users." + p.getName() + "." + timer);
                    }
                    if (plugin.getDebug() == 3) {
                        plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.update", p.getName()));
                    }
                }
            }
            if (plugin.getDebug() >= 1) {
                plugin.getLogger().info(plugin.getCipher().getString("runnable.add.update"));
            }
            yaml.forceSave();
        }
    }
}
