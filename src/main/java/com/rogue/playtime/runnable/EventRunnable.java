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
import com.rogue.playtime.callable.ConsoleCommandCallable;
import com.rogue.playtime.data.mysql.MySQL;
import com.rogue.playtime.data.sqlite.SQLite;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class EventRunnable extends BukkitRunnable {

    private Playtime plugin;
    private final String name;
    private final Integer min;
    private final Integer max;
    private final String type;
    private final List<String> run;
    private final boolean doRepeat;

    public EventRunnable(Playtime p, String cname, String timer, Integer minimum, Integer maximum, List<String> commands, boolean repeat) {
        plugin = p;
        name = cname;
        type = timer;
        min = minimum;
        max = maximum;
        run = commands;
        doRepeat = repeat;
    }

    public void run() {
        ArrayList<String> users;
        if (doRepeat) {
            String data = plugin.getDataManager().getDataHandler().getName();
            users = new ArrayList();
            if (data.equals("sqlite")) {
                SQLite db = new SQLite();
                try {
                    db.open();
                    ResultSet ret = db.query("SELECT * FROM `playTime`");
                    while (ret.next()) {
                        if (ret.getInt(type) % min <= max - min) {
                            users.add(ret.getString("username"));
                        }
                    }
                    ret.close();
                    db.close();
                } catch (SQLException e) {
                    if (plugin.getDebug() == 3) {
                        e.printStackTrace();
                    }
                }
            } else if (data.equals("mysql")) {
                MySQL db = new MySQL();
                try {
                    db.open();
                    ResultSet ret = db.query("SELECT * FROM `playTime`");
                    while (ret.next()) {
                        if (ret.getInt(type) % min <= max - min) {
                            users.add(ret.getString("username"));
                        }
                    }
                    ret.close();
                    db.close();
                } catch (SQLException e) {
                    if (plugin.getDebug() == 3) {
                        e.printStackTrace();
                    }
                }
            }
            if (!users.isEmpty()) {
                for (String user : users) {
                    for (String cmd : run) {
                        Bukkit.getScheduler().callSyncMethod(plugin, new ConsoleCommandCallable(cmd.replace("%u", user)));
                    }
                }
            }
        } else {
            users = plugin.getDataManager().getDataHandler().getPlayersInRange(type, min, max);
            if (!users.isEmpty()) {
                for (String user : users) {
                    for (String cmd : run) {
                        Bukkit.getScheduler().callSyncMethod(plugin, new ConsoleCommandCallable(cmd.replace("%u", user)));
                    }
                }
            }
        }
    }
}
