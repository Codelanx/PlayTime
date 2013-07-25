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

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class SQLite {
    
    Connection con = null;
    
    public Connection open() throws SQLException {
        con = DriverManager.getConnection("jdbc:sqlite:" + Playtime.getPlugin().getDataFolder() + File.separator + "users.db");
        return con;
    }

   public boolean checkTable(String tablename) throws SQLException {
        ResultSet count = query("SELECT name FROM sqlite_master WHERE type='table' AND name='" + tablename + "'");
        boolean give = count.first();
        count.close();
        return give;
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
    }
}
