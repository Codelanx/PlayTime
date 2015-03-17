
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
package main.java.com.codelanx.playtime.callable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.json.JSONArray;

/**
 * Code made by gpotter2!
 *
 * @since 1.4.6
 * @author gpotter2
 * @version 1.4.6
 * 
 */

public class UUIDReverse {

    public static String getText(String uuid){
    	String get = "";
    	try {//GETTING TEXT
    		URL url = new URL("https://api.mojang.com/user/profiles/%pseudo%/names".replaceAll("%pseudo%", uuid));
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String line;
		    while ((line = in.readLine()) != null) {
		    	get = get + line;
		    }
		    in.close();
		} catch (MalformedURLException e){
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
		if(get == null || get.equals(null) || get.equals("")){
			return null;
		}
		return get;
    }
	
    /**
     * Get the username of a Minecraft UUID
     * Will return the username or null if the username doesn't exist in Mojang database !
     *
     * @since 1.4.6
     * @author gpotter2
     * @version 1.4.6
     * 
     */
    
    public static String usernameFromMinecraftUUID(String uuid, Playtime plugin){
    	if(uuid != null){
	    	if(!plugin.isUUID(uuid)){
	    		new IllegalArgumentException("The string must be an uuid !").printStackTrace();
	    		return null;
	    	}
	    	return usernameFromMinecraftUUID(UUID.fromString(uuid));
    	}
    	return null;
    }
    
    /**
     * Get the username of a Minecraft UUID
     * Will return the username or null if the username doesn't exist in Mojang database !
     *
     * @since 1.4.6
     * @author gpotter2
     * @version 1.4.6
     * 
     */
    
    public static String usernameFromMinecraftUUID(UUID uuid){
    	if(uuid == null){
    		return null;
    	}
		String get =  getText(uuid.toString().replaceAll("-", ""));
		if(get == null || get.equals("")){
			return null;
		}
		JSONArray array = new JSONArray(get);
		if(array.length() == 0){
			return null;
		}
		if(array.getJSONObject(0).has("name")){
			return array.getJSONObject(array.length() - 1).getString("name");
		}
		return null;
	}
}