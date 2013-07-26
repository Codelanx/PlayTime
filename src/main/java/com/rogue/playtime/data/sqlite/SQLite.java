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
package com.rogue.playtime.data.sqlite;

import com.rogue.playtime.Playtime;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class SQLite {
    
    private static int connections = 0;
    Connection con = null;
    
    public Connection open() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SQLite.class.getName()).log(Level.SEVERE, "Error loading sqlite connection, disabling!", ex);
            Bukkit.getServer().getPluginManager().disablePlugin(Playtime.getPlugin());
        }
        con = DriverManager.getConnection("jdbc:sqlite:" + Playtime.getPlugin().getDataFolder() + File.separator + "users.db");
        if (Playtime.getPlugin().getDebug() >= 2) {
            Playtime.getPlugin().getLogger().log(Level.INFO, "Open connections: {0}", ++connections);
        }
        return con;
    }

   public boolean checkTable(String tablename) throws SQLException {
        ResultSet count = query("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tablename + "'");
        int i = count.getInt(1);
        if (i > 0) {
            return true;
        }
        return false;
    }

    public ResultSet query(String query) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeQuery(query);
    }
    
    public int update(String query) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeUpdate(query);
    }

    public void close() throws SQLException {
        con.close();
        if (Playtime.getPlugin().getDebug() >= 2) {
            Playtime.getPlugin().getLogger().log(Level.INFO, "Open connections: {0}", --connections);
        }
    }
}
