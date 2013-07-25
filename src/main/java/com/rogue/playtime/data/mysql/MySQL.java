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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @since 1.1
 * @author 1Rogue
 * @version 1.1
 */
public class MySQL {

    Connection con = null;

    public Connection open() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", SQL_Vars.USER);
        connectionProps.put("password", SQL_Vars.PASS);

        con = DriverManager.getConnection("jdbc:mysql://" + SQL_Vars.HOST + ":" + SQL_Vars.PORT + "/" + SQL_Vars.DATABASE, connectionProps);
        return con;
    }

    public boolean checkTable(String tablename) throws SQLException {
        ResultSet count = query("SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = '" + SQL_Vars.DATABASE + "') AND (TABLE_NAME = '" + tablename + "')");
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

    public boolean checkConnection() throws SQLException {
        ResultSet count = query("SELECT count(*) FROM information_schema.SCHEMATA");
        boolean give = count.first();
        count.close();
        return give;
    }
}
