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
package main.java.com.codelanx.playtime.data;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.data.mysql.Data_MySQL;
import main.java.com.codelanx.playtime.data.sqlite.Data_SQLite;
import main.java.com.codelanx.playtime.data.yaml.Data_YAML;
import main.java.com.codelanx.playtime.runnable.ConvertToRunnable;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class DataManager {

    protected final Playtime plugin;
    protected DataHandler data;

    /**
     * The constuctor for DataManager.
     *
     * @since 1.3.0
     * @version 1.4.0
     *
     * @param p The main plugin instance
     * @param automatic Whether to automatically select and run necessary
     * operations
     */
    public DataManager(Playtime p, boolean automatic) {
        this.plugin = p;
        if (automatic) {
            startData();
        }
    }

    /**
     * Begins the process for selecting the data manager and starting necessary
     * processes.
     *
     * Flatfile storage is currently broken, thus it will only load mysql or
     * sqlite. If the type set is flatfile, it will let the user know what it is
     * doing, and convert any yaml data to sqlite.
     *
     * @since 1.4.0
     * @version 1.4.0
     */
    private void startData() {
        this.select(this.plugin.getConfigurationLoader().getString("data.manager"));
        this.setup();
        this.start();

    }

    /**
     * Selects the proper data manager to use, based on the configuration set by
     * the user. By default, it will use flatfile if the user enters something
     * that isn't compatible.
     *
     * @since 1.3.0
     * @version 1.4.0
     *
     * @param type The type of data manager to use
     */
    public void select(String type) {
        type = type.toLowerCase();
        if (type.equals("mysql")) {
            this.data = new Data_MySQL(this.plugin);
        } else if (type.equals("sqlite")) {
            this.data = new Data_SQLite(this.plugin);
        } else {
        	this.data = new Data_YAML(this.plugin);
        }
    }

    /**
     * Runs the startup process for the data manager at hand.
     *
     * @since 1.3.0
     * @version 1.3.0
     */
    public void setup() {
        this.data.init();
        this.data.verifyFormat();
    }

    /**
     * Starts the data updating process
     *
     * @since 1.4.0
     * @version 1.4.0
     */
    public void start() {
        this.data.startRunnables();
    }

    /**
     * Gets the interface in use for handling data. (MySQL, SQLite, or YAML)
     *
     * @since 1.4.0
     * @version 1.4.0
     *
     * @return Data Handler
     */
    public DataHandler getDataHandler() {
        return this.data;
    }

    /**
     * Starts the conversion process from one data type to another
     *
     * @since 1.4.0
     * @version 1.4.0
     *
     * @param newType The new data type (mysql, sqlite, or flatfile)
     * @param players Any players to notify after the completion
     */
    public void convertData(String newType, String... players) {
        this.data.startConversion(newType, players);
    }

    /**
     * Executes the asynchronous converter for Playtime
     *
     * @since 1.4.0
     * @version 1.4.0
     *
     * @param newType The new data type (mysql, sqlite, or flatfile)
     * @param query The query to be used in the conversion process
     * @param players Any players to notify after the completion
     */
    public void convertTo(String newType, String query, String... players) {
        this.plugin.getExecutiveManager().runAsyncTask(new ConvertToRunnable(newType, plugin, query, players), 0L);
    }
}
