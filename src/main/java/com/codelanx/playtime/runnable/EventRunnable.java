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
package com.codelanx.playtime.runnable;

import com.codelanx.playtime.Playtime;
import com.codelanx.playtime.callable.ConsoleCommandCallable;
import com.codelanx.playtime.callable.SendMessageCallable;
import com.codelanx.playtime.data.mysql.MySQL;
import com.codelanx.playtime.data.sqlite.SQLite;
import com.codelanx.playtime.event.EventHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class EventRunnable implements Runnable {

    private final Playtime plugin;
    private final String eventName;
    private final Integer minimum;
    private final Integer maximum;
    private final String timer;
    private final List<String> commands;
    private final boolean repeat;

    public EventRunnable(Playtime plugin, String eventName, String timer, Integer minimum, Integer maximum, List<String> commands, boolean repeat) {
        this.plugin = plugin;
        this.eventName = eventName;
        this.timer = timer;
        this.minimum = minimum;
        this.maximum = maximum;
        this.commands = commands;
        this.repeat = repeat;
    }

    public void run() {
        this.plugin.getLogger().log(Level.SEVERE, "{0} was called, but is disabled!", this.getClass().getSimpleName());
        /*Map<String, Integer> users;
        EventHandler event = this.plugin.getEventHandler();
        if (this.repeat) {
            String data = this.plugin.getDataManager().getDataHandler().getName();
            users = new HashMap();
            if (data.equals("sqlite")) {
                SQLite db = new SQLite();
                try {
                    db.open();
                    ResultSet ret;
                    if (this.minimum == 1 || this.minimum == 0) {
                        ret = db.query("SELECT * FROM `playTime` WHERE `"
                                + this.timer + "` >= 1");
                    } else {
                        ret = db.query("SELECT * FROM `playTime` WHERE (`"
                                + this.timer + "`%" + this.minimum + ") <= "
                                + (this.maximum - this.minimum - 1)
                                + " AND `"
                                + this.timer + "`>= "
                                + this.minimum);
                    }
                    while (ret.next()) {
                        users.put(ret.getString("username"), ret.getInt(this.timer));
                    }
                    ret.close();
                } catch (SQLException e) {
                    this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
                } finally {
                    db.close();
                }
            } else if (data.equals("mysql")) {
                MySQL db = new MySQL();
                try {
                    db.open();
                    ResultSet ret;
                    if (this.minimum == 1 || this.minimum == 0) {
                        ret = db.query("SELECT * FROM `playTime` WHERE `"
                                + this.timer
                                + "` >= 1");
                    } else {
                        ret = db.query("SELECT * FROM `playTime` WHERE MOD(`"
                                + this.timer + "`, " + this.minimum + ") <= "
                                + (this.maximum - this.minimum - 1)
                                + " AND `" + this.timer
                                + "` >= " + this.minimum);
                    }
                    while (ret.next()) {
                        users.put(ret.getString("username"), ret.getInt(this.timer));
                    }
                    ret.close();
                } catch (SQLException e) {
                    this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
                } finally {
                    db.close();
                }
            }
            if (!users.isEmpty()) {
                for (String user : users.keySet()) {
                    Player p = Bukkit.getPlayer(user);
                    if (p.isOnline()) {
                        for (String cmd : this.commands) {
                            if (event.isMessage(cmd.split(" ")[0])) {
                                this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(user, 
                                        event.replaceMessage(cmd)
                                        .replace("%u", user)
                                        .replace("%t", this.plugin.getEventHandler().toReadable(users.get(user)))
                                        ), 0L);
                            } else {
                                this.plugin.getExecutiveManager().runCallable(new ConsoleCommandCallable(
                                        cmd.replace("%u", user)
                                        .replace("%t", this.plugin.getEventHandler()
                                        .toReadable(users.get(user)))
                                        ), 0L);
                            }
                        }
                    }
                }
            }
        } else {
            users = this.plugin.getDataManager().getDataHandler().getPlayersInRange(this.timer, this.minimum, this.maximum);
            if (!users.isEmpty()) {
                for (String user : users.keySet()) {
                    Player p = Bukkit.getPlayer(user);
                    if (p.isOnline()) {
                        for (String cmd : this.commands) {
                            if (event.isMessage(cmd.split(" ")[0])) {
                                this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(user,
                                        event.replaceMessage(cmd)
                                        .replace("%u", user)
                                        .replace("%t", this.plugin.getEventHandler()
                                        .toReadable(users.get(user)))
                                        ), 0L);
                            } else {
                                this.plugin.getExecutiveManager().runCallable(new ConsoleCommandCallable(
                                        cmd.replace("%u", user)
                                        .replace("%t", this.plugin.getEventHandler()
                                        .toReadable(users.get(user)))
                                        ), 0L);
                            }
                        }
                    }
                }
            }
        }*/
    }
}