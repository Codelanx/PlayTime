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

/**
 *
 * @since 1.1
 * @author 1Rogue
 * @version 1.1
 */
public class SQL_Vars {
    
    public static String HOST = "";
    public static String USER = "";
    public static String PASS = "";
    public static String DATABASE = "";
    public static String PORT = "";
    
    public void setVars(String host, String user, String pass, String database, String port) {
        HOST = host;
        USER = user;
        PASS = pass;
        DATABASE = database;
        PORT = port;
    }
    
}
