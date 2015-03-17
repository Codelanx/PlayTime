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
package main.java.com.codelanx.playtime.runnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.callable.UUIDFetcher;
import main.java.com.codelanx.playtime.data.mysql.MySQL;
import main.java.com.codelanx.playtime.data.sqlite.SQLite;
import main.java.com.codelanx.playtime.data.yaml.Data_YAML;
import main.java.com.codelanx.playtime.data.yaml.YAML;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.5.0
 */
public class AddRunnable implements Runnable {

    private final Playtime plugin;
    private final boolean afkEnabled;
    private final ArrayList<String> timers;

    public AddRunnable(Playtime plugin) {
        this.plugin = plugin;
        this.afkEnabled = this.plugin.getPlayerHandler() != null;
        this.timers = new ArrayList<String>();
        this.timers.add("playtime");
        if (this.plugin.getConfigurationLoader().getBoolean("check.death-time")) {
            this.timers.add("deathtime");
        }
        if (this.plugin.getConfigurationLoader().getBoolean("check.online-time")) {
            this.timers.add("onlinetime");
        }
    }

    public void run() {
        String current = this.plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();
            MySQL db = new MySQL();
            StringBuilder sb = new StringBuilder("INSERT INTO `playTime` (`username`, `uuid`) VALUES ");
            if (players.size() > 0) {
                for (Player p : players) {
                    if (!this.afkEnabled || !this.plugin.getPlayerHandler().isAFK(p.getName())) {
                        sb.append("('").append(p.getName()).append("', '").append(p.getUniqueId()).append("'), ");
                    }
                }
                if (sb.toString().endsWith(" VALUES ")) {
                    if (this.plugin.getDebug() >= 1) {
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.none"));
                    }
                    return;
                }
                sb = new StringBuilder(sb.substring(0, sb.length() - 2));
                sb.append(" ON DUPLICATE KEY UPDATE ");
                for (String timer : this.timers) {
                    sb.append("`").append(timer).append("`=`").append(timer).append("`+1, ");
                }
                sb = new StringBuilder(sb.substring(0, sb.length() - 2));
                if (this.plugin.getDebug() >= 1) {
                    this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.update"));
                    if (this.plugin.getDebug() >= 2) {
                        this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.add.query", sb.toString()));
                    }
                }
            } else {
                if (this.plugin.getDebug() >= 1) {
                    this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.none"));
                }
                return;
            }
            try {
                db.open();
                db.update(sb.toString());
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? ex : "null");
            } finally {
                db.close();
            }
        } else if (current.equals("sqlite")) {
            Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();
            if (players.size() > 0) {
                StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO `playTime` ");
                StringBuilder sb2 = new StringBuilder("UPDATE `playTime` SET ");
                for (String timer : this.timers) {
                    sb2.append("`").append(timer).append("`=`").append(timer).append("`+1, ");
                }
                sb2 = new StringBuilder(sb2.substring(0, sb2.length() - 2));
                sb2.append(" WHERE `uuid` IN (");
                int i = 0;
                for (Player p : players) {
                    if (!this.afkEnabled || !this.plugin.getPlayerHandler().isAFK(p.getName())) {
                        sb2.append("'").append(p.getUniqueId()).append("', ");
                        if (i > 0) {
                            sb.append("UNION SELECT NULL, '").append(p.getName()).append("', '").append(p.getUniqueId()).append("', 0, 0, 0 ");
                        } else {
                            sb.append("SELECT NULL AS 'column1', '").append(p.getName()).append("' AS 'column2', '").append(p.getUniqueId()).append("' AS 'column3', 0 AS 'column4', 0 AS 'column5', 0 AS 'column6' ");
                            i++;
                        }
                    }
                }
                if (sb.toString().endsWith(" `playTime` ")) {
                    if (this.plugin.getDebug() >= 1) {
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.none"));
                    }
                    return;
                }
                if (this.plugin.getDebug() >= 2) {
                    this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.add.query", sb.substring(0, sb.length() - 1)));
                    this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.add.query", sb2.substring(0, sb2.length() - 2) + ")"));
                }
                SQLite db = new SQLite();
                try {
                    db.open();

                    db.update(sb.substring(0, sb.length() - 1));
                    db.update(sb2.substring(0, sb2.length() - 2) + ")");

                    if (this.plugin.getDebug() >= 1) {
                        this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.update"));
                    }
                } catch (SQLException ex) {
                    this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? ex : "null");
                } finally {
                    db.close();
                }
            }
        } else if (current.equals("flatfile")) {
            YAML yaml = ((Data_YAML) this.plugin.getDataManager().getDataHandler()).getYaml();
            Collection<? extends Player> players = this.plugin.getServer().getOnlinePlayers();
            for (Player p : players) {
                if (!this.afkEnabled || !this.plugin.getPlayerHandler().isAFK(p.getName())) {
                    for (String timer : this.timers) {
                    	if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
	                        try {
								yaml.incrementValue("users." + UUIDFetcher.getUUIDOf(p.getName()) + "." + timer);
							} catch (Exception e) {
								e.printStackTrace();
							}
                    	} else {
	                        try {
								yaml.incrementValue("users." + p.getUniqueId() + "." + timer);
							} catch (Exception e) {
								e.printStackTrace();
							}
                    	}
                    }
                    if (this.plugin.getDebug() == 3) {
                        this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.add.update", p.getName()));
                    }
                }
            }
            if (this.plugin.getDebug() >= 1) {
                this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.add.update"));
            }
            yaml.forceSave();
        }
    }
}
