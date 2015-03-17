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
import java.util.logging.Level;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.callable.SendMessageCallable;
import main.java.com.codelanx.playtime.data.DataManager;
import main.java.com.codelanx.playtime.data.mysql.MySQL;
import main.java.com.codelanx.playtime.data.sqlite.SQLite;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.5.0
 */
public class ConvertToRunnable implements Runnable {

    private final Playtime plugin;
    private final String[] players;
    private final String query;
    private final String newType;

    public ConvertToRunnable(String newType, Playtime plugin, String query, String... players) {
        this.newType = newType;
        this.plugin = plugin;
        this.query = query;
        this.players = players;
    }

    public void run() {
        DataManager dm = new DataManager(this.plugin, false);
        if (this.newType.equals("mysql")) {
            dm.select(this.newType);
            dm.setup();

            MySQL db = new MySQL();
            try {
                db.open();
                db.update("TRUNCATE TABLE `playTime`");
                db.update(this.query);
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
            } finally {
                db.close();
            }
            this.plugin.reload();
        } else if (this.newType.equals("sqlite")) {
            this.plugin.getConfigurationLoader().getConfig().set("data.manager", this.newType);
            this.plugin.getConfigurationLoader().saveConfig();
            dm.select(this.newType);
            dm.setup();

            SQLite db = new SQLite();
            try {
                db.open();
                db.update("DELETE FROM `playTime`");
                String[] queries = this.query.split("\n");
                for (String p : this.players) {
                    this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, this.plugin.getCipher().getString("runnable.convertto.rows", queries.length, getTime(queries.length))), 0L);
                }
                for (String s : queries) {
                    db.update(s);
                }
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, "{0}", this.plugin.getDebug() >= 3 ? e : "null");
            } finally {
                db.close();
            }
            this.plugin.reload();
            for (String p : this.players) {
                this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(p, this.plugin.getCipher().getString("runnable.convertto.complete")), 0L);
            }
        } else if (this.newType.equals("flatfile")) {
            for (String s : this.players) {
                this.plugin.getExecutiveManager().runCallable(new SendMessageCallable(s, this.plugin.getCipher().getString("runnable.convertto.noflat")), 0L);
            }
        }
        this.plugin.getConfigurationLoader().getConfig().set("data.manager", this.newType);
        this.plugin.getConfigurationLoader().saveConfig();
        this.plugin.setBusy(false);
    }

    /**
     * Returns an estimation of how long SQLite conversion will take based on
     * the number of rows required to add. The converter uses individual INSERT
     * statements for each row, which is unfortunate due to SQLite's limitations
     * on multiple values within inserts or union selects.
     *
     * @since 1.4.0
     * @version 1.4.1
     *
     * @param rows The number of rows to evaluate
     * @return The estimated time as a readable string
     */
    private String getTime(int rows) {
        long time = Math.round(rows / (369 + ((2 / 3) - 0.2)));
        long seconds = time % 60;
        long minutes = time / 60;
        return ((minutes >= 1) ? minutes + " " + ((minutes != 1) ? this.plugin.getCipher().getString("variables.minutes") : this.plugin.getCipher().getString("variables.minute")) + " " : "") + seconds + " " + ((seconds != 1) ? this.plugin.getCipher().getString("variables.seconds") : this.plugin.getCipher().getString("variables.second")) + ".";
    }
}
