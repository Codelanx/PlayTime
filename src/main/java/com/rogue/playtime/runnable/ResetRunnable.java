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

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ResetRunnable implements Runnable {

    private final Playtime plugin;
    private final String username;
    private final String column;

    public ResetRunnable(Playtime plugin, String username, String column) {
        this.plugin = plugin;
        this.username = username;
        this.column = column;
    }

    public void run() {
        String current = this.plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            MySQL db = new MySQL();
            try {
                db.open();
                db.update("UPDATE `playTime` SET `" + this.column + "`=0 WHERE `username`='" + this.username + "'");
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, this.plugin.getCipher().getString("runnable.reset.error", this.column.substring(0, this.column.length() - 5)), this.plugin.getDebug() >= 3 ? ex : "");
            } finally {
                db.close();
            }
        } else if (current.equals("sqlite")) {
            SQLite db = new SQLite();
            try {
                db.open();
                db.update("UPDATE `playTime` SET `" + this.column + "`=0 WHERE `username`='" + this.username + "'");
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, this.plugin.getCipher().getString("runnable.reset.error", this.column.substring(0, this.column.length() - 5)), this.plugin.getDebug() >= 3 ? ex : "");
            } finally {
                db.close();
            }
        } else if (current.equals("flatfile")) {
            YAML yaml = new YAML();
            yaml.getFile().set("users." + this.username + "." + this.column, 0);
            yaml.forceSave();
        }
    }
}
