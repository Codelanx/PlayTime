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
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class AddRunnable extends BukkitRunnable {

    Playtime plugin;

    public AddRunnable(Playtime p) {
        plugin = p;
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
                    if (plugin.isAFKEnabled()) {
                        if (!plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                            sb.append("('").append(p.getName()).append("'), ");
                        }
                    } else {
                        sb.append("('").append(p.getName()).append("'), ");
                    }
                }
                if (sb.toString().endsWith(" VALUES ")) {
                    if (plugin.getDebug() >= 1) {
                        plugin.getLogger().info(plugin.getCipher().getString("runnable.add.none"));
                    }
                    return;
                }
                if (plugin.getDebug() >= 1) {
                    plugin.getLogger().info(plugin.getCipher().getString("runnable.add.update"));
                    if (plugin.getDebug() >= 2) {
                        plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.query", sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1" + (plugin.isDeathEnabled() ? ", `deathtime`=`deathtime`+1" : "") + ", `onlinetime`=`onlinetime`+1"));
                    }
                }
                complete = true;
            }
            try {
                db.open();
                if (db.checkConnection() && complete) {
                    db.update(sb.substring(0, sb.length() - 2) + " ON DUPLICATE KEY UPDATE `playtime`=`playtime`+1" + (plugin.isDeathEnabled() ? ", `deathtime`=`deathtime`+1" : "") + ", `onlinetime`=`onlinetime`+1");
                }
                db.close();
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, null);
                if (plugin.getDebug() == 3) {
                    ex.printStackTrace();
                }
            }
        } else if (current.equals("sqlite")) {
            Player[] players = plugin.getServer().getOnlinePlayers();
            if (players.length > 0) {
                StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO `playTime` ");
                StringBuilder sb2 = new StringBuilder("UPDATE `playTime` SET `playtime`=`playtime`+1");
                sb2.append((plugin.isDeathEnabled()) ? ", `deathtime`=`deathtime`+1" : "").append(", `onlinetime`=`onlinetime`+1 WHERE username IN (");
                int i = 0;
                for (Player p : players) {
                    if ((plugin.isAFKEnabled()) && !plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                        sb2.append("'").append(p.getName()).append("', ");
                        if (i > 0) {
                            sb.append("UNION SELECT NULL, '").append(p.getName()).append("', 0, 0, 0 ");
                        } else {
                            sb.append("SELECT NULL AS 'column1', '").append(p.getName()).append("' AS 'column2', 0 AS 'column3', 0 AS 'column4', 0 AS 'column5' ");
                            i++;
                        }
                    } else if (!plugin.isAFKEnabled()) {
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
                if (plugin.isAFKEnabled()) {
                    if (!plugin.getPlayerHandler().getPlayer(p.getName()).isAFK()) {
                        if (plugin.isDeathEnabled()) {
                            yaml.incrementValue("users." + p.getName() + ".playtime");
                            yaml.incrementValue("users." + p.getName() + ".deathtime");
                            yaml.incrementValue("users." + p.getName() + ".onlinetime");
                            if (plugin.getDebug() == 3) {
                                plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.update", p.getName()));
                            }
                        } else {
                            yaml.incrementValue("users." + p.getName() + ".playtime");
                            yaml.incrementValue("users." + p.getName() + ".onlinetime");
                            if (plugin.getDebug() == 3) {
                                plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.update", p.getName()));
                            }
                        }
                    }
                } else {
                    if (plugin.isDeathEnabled()) {
                        yaml.incrementValue("users." + p.getName() + ".playtime");
                        yaml.incrementValue("users." + p.getName() + ".deathtime");
                        yaml.incrementValue("users." + p.getName() + ".onlinetime");
                        if (plugin.getDebug() == 3) {
                            plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.update", p.getName()));
                        }
                    } else {
                        yaml.incrementValue("users." + p.getName() + ".playtime");
                        yaml.incrementValue("users." + p.getName() + ".onlinetime");
                        if (plugin.getDebug() == 3) {
                            plugin.getLogger().log(Level.INFO, plugin.getCipher().getString("runnable.add.update", p.getName()));
                        }
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
