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
package com.rogue.playtime.data.mysql;

import com.rogue.playtime.Playtime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Instantiable MySQL connector
 *
 * @since 1.1
 * @author 1Rogue
 * @version 1.1
 */
public class MySQL {

    private static int connections = 0;
    private Connection con = null;

    /**
     * Opens a connection to the MySQL database. Make sure to call MySQL.close()
     * after you are finished working with the database for your segment of your
     * code.
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @return The Connection object
     * @throws SQLException 
     */
    public Connection open() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", MySQL_Vars.USER);
        connectionProps.put("password", MySQL_Vars.PASS);

        con = DriverManager.getConnection("jdbc:mysql://" + MySQL_Vars.HOST + ":" + MySQL_Vars.PORT + "/" + MySQL_Vars.DATABASE, connectionProps);
        if (Playtime.getPlugin().getDebug() >= 2) {
            Playtime.getPlugin().getLogger().log(Level.INFO, "Open connections: {0}", ++connections);
        }
        return con;
    }

    /**
     * Checks if a table exists within the set database
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @param tablename Name of the table to check for
     * @return true if exists, false otherwise
     * @throws SQLException 
     */
    public boolean checkTable(String tablename) throws SQLException {
        ResultSet count = query("SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = '" + MySQL_Vars.DATABASE + "') AND (TABLE_NAME = '" + tablename + "')");
        int i = 0;
        if (count.next()) {
            i = count.getInt(1);
        }
        count.close();
        return (i == 1) ? true : false;
    }

    /**
     * Executes a query, but does not update any information
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @param query The string query to execute
     * @return A ResultSet from the query
     * @throws SQLException 
     */
    public ResultSet query(String query) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeQuery(query);
    }
    
    /**
     * Executes a query that can change values
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @param query The string query to execute
     * @return 0 for no returned results, or the number of returned rows
     * @throws SQLException 
     */
    public int update(String query) throws SQLException {
        Statement stmt = con.createStatement();
        return stmt.executeUpdate(query);
    }
    
    /**
     * Closes the MySQL connection. Must be open first.
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @throws SQLException 
     */
    public void close() throws SQLException {
        con.close();
        if (Playtime.getPlugin().getDebug() >= 2) {
            Playtime.getPlugin().getLogger().log(Level.INFO, "Open connections: {0}", --connections);
        }
    }
    
    /**
     * Checks to make sure the connection is active to the MySQL server
     * 
     * @since 1.1
     * @version 1.1
     * 
     * @return true if connected, false otherwise
     * @throws SQLException 
     */
    public boolean checkConnection() throws SQLException {
        ResultSet count = query("SELECT count(*) FROM information_schema.SCHEMATA");
        boolean give = count.first();
        count.close();
        return give;
    }
}
