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
package com.rogue.playtime2.update;

import com.rogue.playtime2.Playtime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Handles the update process for {@link Playtime}
 *
 * @since 1.4.5
 * @author 1Rogue
 * @version 1.4.5
 */
public class UpdateHandler {

    protected final Playtime plugin;
    protected final Choice choice;
    protected Result result = Result.INCOMPLETE;
    protected final String file;
    protected final int id;
    protected byte debug = 0;

    /**
     * Constructor for {@link UpdateHandler}
     *
     * @since 1.4.5
     * @version 1.4.5
     *
     * @param plugin The main {@link Playtime} instance
     * @param choice The downloading option to use
     * @param id The project id
     * @param file The name of the current plugin file
     */
    public UpdateHandler(Playtime plugin, Choice choice, int id, String file) {
        this.plugin = plugin;
        this.choice = choice;
        this.id = id;
        this.file = file;
    }
    
    /**
     * Runs an update check. Should only be called once (have not made a good work-around yet)
     * 
     * @since 1.4.5
     * @version 1.4.5
     */
    public void runCheck() {
        UpdateRunnable ur = new UpdateRunnable(this.plugin, choice, this.id, this.file);
        ur.setDebug(this.debug);
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin,
                ur,
                10L);
    }

    /**
     * Handles the appropriate response to an update {@link Result}
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @param result The update {@link Result}
     */
    protected final void handleUpdate(Result result) {
        this.result = result;
        if (result == Result.UPDATE_AVAILABLE) {
            this.registerNewNotifier();
        } else {
            result.handleUpdate(this.plugin.getLogger());
        }
    }

    /**
     * Registers a new {@link UpdateListener} about an available update
     * 
     * @since 1.4.5
     * @version 1.4.5
     */
    protected final void registerNewNotifier() {
        this.plugin.getListenerManager().registerListener("update",
                new UpdateListener("A new update is available for "
                        + this.plugin.getDescription().getFullName()
                        + "!"));
    }
    
    /**
     * Returns the {@link Result} from the update check
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @return The update {@link Result}
     */
    public final Result getUpdateStatus() {
        return this.result;
    }

    /**
     * Sets the debug level of this update check
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @param debug The debug level to set
     */
    public final void setDebug(byte debug) {
        this.debug = debug;
    }

}

/**
 * Runs an update check
 *
 * @since 1.4.5
 * @author 1Rogue
 * @version 1.4.5
 */
class UpdateRunnable extends UpdateHandler implements Runnable {

    private final String VERSION_URL;
    private final String DL_URL = "downloadUrl";
    private final String DL_FILE = "fileName";
    private final String DL_NAME = "name";
    private JSONObject latest;

    /**
     * Constructor for {@link UpdateRunnable}
     *
     * @since 1.4.5
     * @version 1.4.5
     *
     * @param plugin The {@link Playtime} instance
     * @param choice The {@link Choice} for downloading
     * @param id The project id
     * @param file The name of the plugin file
     */
    public UpdateRunnable(Playtime plugin, Choice choice, int id, String file) {
        super(plugin, choice, id, file);
        this.result = Result.NO_UPDATE;
        this.VERSION_URL = "https://api.curseforge.com/servermods/files?projectIds=" + id;
    }

    /**
     * Runs the update process
     *
     * @since 1.4.5
     * @version 1.4.5
     */
    public void run() {
        boolean current = false;
        if (!this.choice.equals(Choice.NO_UPDATE)) {
            this.getJSON();
            if (this.latest != null) {
                if (this.choice.doCheck()) {
                    this.result = this.checkVersion();
                }
                if (this.choice.doDownload() && this.result == Result.UPDATE_AVAILABLE) {
                    this.result = this.download();
                }
            }
        }
        this.handleUpdate(this.result);
    }

    /**
     * Downloads the latest jarfile for the {@link Plugin}
     *
     * @since 1.4.5
     * @version 1.4.5
     *
     * @TODO Add zip file support
     * @return The download result
     */
    public Result download() {
        Result back = Result.UPDATED;
        File updateLoc = this.plugin.getServer().getUpdateFolderFile();
        updateLoc.mkdirs();
        String url = (String) this.latest.get(this.DL_URL);
        File location = new File(updateLoc, this.file);
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            URL call = new URL(url);
            rbc = Channels.newChannel(call.openStream());
            fos = new FileOutputStream(location);
            fos.getChannel().transferFrom(rbc, 0, 1 << 24);
        } catch (MalformedURLException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error finding plugin update to download!", ex);
            back = Result.ERROR_FILENOTFOUND;
        } catch (IOException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error transferring plugin data!", ex);
            back = Result.ERROR_DOWNLOAD_FAILED;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (rbc != null) {
                    rbc.close();
                }
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Error closing streams/channels for download!", ex);
            }
        }
        return back;
    }

    /**
     * Checks the current {@link Plugin} version against the latest live version
     *
     * @since 1.4.5
     * @version 1.4.5
     *
     * @return The {@link Result} of the version check
     */
    private Result checkVersion() {
        Result back = Result.NO_UPDATE;
        String curVersion = this.plugin.getDescription().getVersion();
        String file = (String) this.latest.get(this.DL_NAME);
        String last = file.substring(file.lastIndexOf("-") + 1, file.length());
        if (newVersion(curVersion, last)) {
            back = Result.UPDATE_AVAILABLE;
        }
        return back;
    }
    
    /**
     * Compares two string versions to determine which is newer. Keep in mind
     * that conventions of different lengths are considered. The default approach
     * is that if both numbers are the same up until the extraneous number, the
     * longer version will be considered "newer". For example:
     * <ul>
     *   <li> 1.2.3 </li>
     *   <li> 1.2.3.1 </li>
     * </ul>
     * 
     * <p>The second option would be considered "newer". Keep in mind if the
     * second example was "1.2.3.0", it would still be considered newer.</p>
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @param v1 The original version
     * @param v2 The new version to compare
     * @return True if v2 is newer, false otherwise
     */
    private boolean newVersion(String v1, String v2) {
        this.plugin.getLogger().log(Level.INFO, "Original version: {0}", v1);
        this.plugin.getLogger().log(Level.INFO, "New version: {0}", v2);
        String[] v1tot = v1.split("\\.");
        String[] v2tot = v2.split("\\.");
        this.plugin.getLogger().log(Level.INFO, "Running check loop...");
        for (int i = 0; i < v1tot.length && i < v2tot.length; i++) {
            this.plugin.getLogger().log(Level.INFO, "Comparing {0} to {1}", new String[]{v1tot[i], v2tot[i]});
            if (this.getInt(v1tot[i]) < this.getInt(v2tot[i])) {
                return true;
            }
        }
        this.plugin.getLogger().log(Level.INFO, "check loop complete, no different found. Checking lengths...");
        if (v1tot.length != v2tot.length) {
            return v1tot.length < v2tot.length;
        }
        return false;
    }
    
    /**
     * Gets an integer from a string, or returns 0 if it is not a number
     * 
     * @since 1.4.5
     * @version 1.4.5
     * 
     * @param s The string to convert
     * @return The numeric value, or 0 if there is no comprehensible value
     */
    private int getInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            this.plugin.getLogger().log(Level.SEVERE, "Error parsing input '{0}'!", s);
            return 0;
        }
    }

    /**
     * Gets the {@link JSONObject} from the CurseAPI of the newest project version.
     * 
     * @since 1.4.5
     * @version 1.4.5
     */
    private void getJSON() {
        InputStream stream = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        String json = null;
        try {
            URL call = new URL(this.VERSION_URL);
            stream = call.openStream();
            isr = new InputStreamReader(stream);
            reader = new BufferedReader(isr);
            json = reader.readLine();
        } catch (MalformedURLException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Error checking for an update",
                    this.debug >= 3 ? ex : "");
            this.result = Result.ERROR_BADID;
            this.latest = null;
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE,
                    "Error checking for an update",
                    this.debug >= 3 ? ex : "");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                this.plugin.getLogger().log(Level.SEVERE,
                        "Error closing updater streams!",
                        this.debug >= 3 ? ex : "");
            }
        }
        if (json != null) {
            JSONArray arr = (JSONArray) JSONValue.parse(json);
            this.latest = (JSONObject) arr.get(arr.size() - 1);
        } else {
            this.latest = null;
        }
    }

}
