/*
 * Copyright (C) 2014 Spencer
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


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.callable.UUIDFetcher;
import main.java.com.codelanx.playtime.data.sqlite.SQLite;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

/**
 * Class description for {@link UUIDRunnable}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class UUIDRunnable implements Runnable {

    private final Playtime plugin;

    public UUIDRunnable(Playtime plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public void run() {
        List<String> names = new LinkedList<String>();
        String type = this.plugin.getDataManager().getDataHandler().getName();
        if (type.equalsIgnoreCase("mysql")) {
            MySQL db = new MySQL(this.plugin);
            try {
                db.open();
                ResultSet r = db.query("SELECT `username` FROM `playTime` WHERE `uuid`=''");
                while (r.next()) {
                    names.add(r.getString("username"));
                }
            } catch (SQLException ex) {
                return;
            } finally {
                db.close();
            }
        } else if (type.equalsIgnoreCase("sqlite")) {
            SQLite db = new SQLite(this.plugin);
            try {
                db.open();
                ResultSet r = db.query("SELECT `username` FROM `playTime` WHERE `uuid`=''");
                while (r.next()) {
                    names.add(r.getString("username"));
                }
            } catch (SQLException ex) {

            } finally {
                db.close();
            }
        } else {
            //wot
            return;
        }
        Map<String, UUID> converted = new HashMap<String, UUID>();

        for (Iterator<String> itr = names.iterator(); itr.hasNext();) {
            String name = itr.next();
            OfflinePlayer p;
            if(plugin.isUUID(name)){
            	p = Bukkit.getPlayer(UUID.fromString(name));
            } else {
            	p = Bukkit.getPlayer(name);
            }
            if (p.hasPlayedBefore()) {
                converted.put(name, p.getUniqueId());
                itr.remove();
            }
        }
        UUIDFetcher uids = new UUIDFetcher(names);
        try {
            converted.putAll(uids.call());
        } catch (Exception ex) {
            Logger.getLogger(UUIDRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (type.equalsIgnoreCase("mysql")) {
            MySQL db = new MySQL(this.plugin);
            try {
                db.open();
                db.getConnection().setAutoCommit(false);
                PreparedStatement ps = db.getConnection().prepareStatement("UPDATE `playTime` SET `uuid`=? WHERE `username`=?");
                Entry<String, UUID> ent;
                for (Iterator<Entry<String, UUID>> itr = converted.entrySet().iterator(); itr.hasNext();) {
                    for (int i = 0; i < 1000; i++) {
                        ent = itr.next();
                        ps.setString(1, ent.getKey());
                        ps.setString(2, ent.getValue().toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    db.getConnection().commit();
                }
                db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                return;
            } finally {
                db.close();
            }
        } else if (type.equalsIgnoreCase("sqlite")) {
            SQLite db = new SQLite(this.plugin);
            try {
                db.open();
                db.getConnection().setAutoCommit(false);
                PreparedStatement ps = db.getConnection().prepareStatement("UPDATE `playTime` SET `uuid`=? WHERE `username`=?");
                Entry<String, UUID> ent;
                for (Iterator<Entry<String, UUID>> itr = converted.entrySet().iterator(); itr.hasNext();) {
                    for (int i = 0; i < 1000; i++) {
                        ent = itr.next();
                        ps.setString(1, ent.getKey());
                        ps.setString(2, ent.getValue().toString());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    db.getConnection().commit();
                }
                db.getConnection().setAutoCommit(true);
            } catch (SQLException ex) {
                return;
            } finally {
                db.close();
            }
        }
    }

    private class MySQL extends main.java.com.codelanx.playtime.data.mysql.MySQL {

        public MySQL(Playtime... plugins) {
            super(plugins);
        }

        @Override
        public Connection open() throws SQLException {
            Properties connectionProps = new Properties();
            connectionProps.put("user", USER);
            connectionProps.put("password", PASS);

            this.con = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE + "?useServerPrepStmts=false&rewriteBatchedStatements=true", connectionProps);
            if (this.plugin.getDebug() >= 2) {
                this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("data.mysql.instance.open", ++connections));
            }
            return this.con;
        }

    }

}
