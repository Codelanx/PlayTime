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
package com.rogue.playtime;

/**
 *
 * @since
 * @author 1Rogue
 * @version
 */
public class SQL_Vars {
    
    protected static String HOST = "";
    protected static String USER = "";
    protected static String PASS = "";
    protected static String DATABASE = "";
    protected static String PORT = "";
    
    public void setVars(String host, String user, String pass, String database, String port) {
        HOST = host;
        USER = user;
        PASS = pass;
        DATABASE = database;
        PORT = port;
    }
    
}
