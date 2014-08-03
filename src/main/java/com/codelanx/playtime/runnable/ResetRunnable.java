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
import com.codelanx.playtime.data.mysql.MySQL;
import com.codelanx.playtime.data.sqlite.SQLite;
import com.codelanx.playtime.data.yaml.YAML;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.5.0
 */
public class ResetRunnable implements Runnable {

    private final Playtime plugin;
    private final UUID uuid;
    private final String column;

    public ResetRunnable(Playtime plugin, UUID uuid, String column) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.column = column;
    }

    public void run() {
        String current = this.plugin.getDataManager().getDataHandler().getName();
        if (current.equals("mysql")) {
            MySQL db = new MySQL();
            try {
                db.open();
                db.update("UPDATE `playTime` SET `" + this.column + "`=0 WHERE `uuid`='" + this.uuid + "'");
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, this.plugin.getCipher().getString("runnable.reset.error", this.column.substring(0, this.column.length() - 5)), this.plugin.getDebug() >= 3 ? ex : "");
            } finally {
                db.close();
            }
        } else if (current.equals("sqlite")) {
            SQLite db = new SQLite();
            try {
                db.open();
                db.update("UPDATE `playTime` SET `" + this.column + "`=0 WHERE `uuid`='" + this.uuid + "'");
            } catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, this.plugin.getCipher().getString("runnable.reset.error", this.column.substring(0, this.column.length() - 5)), this.plugin.getDebug() >= 3 ? ex : "");
            } finally {
                db.close();
            }
        } else if (current.equals("flatfile")) {
            this.plugin.getLogger().log(Level.SEVERE, "{0} attempted to run YML check, which is broken!", this.getClass().getSimpleName());
            /*YAML yaml = new YAML();
            yaml.getFile().set("users." + this.username + "." + this.column, 0);
            yaml.forceSave();*/
        }
    }
}
