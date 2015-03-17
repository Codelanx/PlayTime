/*
 * Copyright (C) 2013 Spencer Alderman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
0 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package main.java.com.codelanx.playtime.data.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.callable.UUIDFetcher;
import main.java.com.codelanx.playtime.callable.UUIDReverse;
import main.java.com.codelanx.playtime.data.DataHandler;
import main.java.com.codelanx.playtime.runnable.AddRunnable;
import main.java.com.codelanx.playtime.runnable.StartConvertRunnable;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;

/**
 * YAML Data Manager. Check DataHandler for information about each method
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.5.0
 * 
 */
public class Data_YAML implements DataHandler {

    private final Playtime plugin;
    private YAML yaml;
    
    public Data_YAML(Playtime plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return "flatfile";
    }

    public int getValue(String data, UUID user) {
        if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
	        try {
	        	String path = "users." + user + "." + data;
	        	return this.yaml.getFile().getInt(path, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
        } else {
        	if(Bukkit.getPlayer(user) == null && !Bukkit.getPlayer(user).isOnline()){
        		return -1;
        	}
        	try {
				return this.yaml.getFile().getInt("users." + Bukkit.getPlayer(user).getUniqueId() + "." + data, -1);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
	public Map<String, Integer> getTopPlayers(String data, byte amount) {
    	ConfigurationSection users = this.yaml.getFile().getConfigurationSection("users");
    	ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
    	for(String actual : users.getKeys(false)){
    		if(plugin.isUUID(actual)){
    			if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
    				String name_temp = UUIDReverse.usernameFromMinecraftUUID(actual, this.plugin);
    				if(name_temp != null){
		    			OfflinePlayer player = Bukkit.getOfflinePlayer(name_temp);
		    			if(player != null) players.add(player);
    				}
    			} else {
    				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(actual));
	    			if(player != null) players.add(player);
    			}
    		} else {
    			OfflinePlayer player = Bukkit.getOfflinePlayer(actual);
    			if(player != null) players.add(player);
    		}
    	}
    	OfflinePlayer[] best_players_tidied = new OfflinePlayer[players.size()];
    	best_players_tidied = tidyList(players.toArray(best_players_tidied), data);
    	HashMap<String, Integer> result = new HashMap<String, Integer>();
    	for(int i = 0; i < amount; i++){
    		if(i >= best_players_tidied.length){
    			break;
    		}
    		OfflinePlayer player = best_players_tidied[i];
    		if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
    			try {
					result.put(player.getName(), getValue(data, UUIDFetcher.getUUIDOf(player.getName())));
				} catch (Exception e) {
					e.printStackTrace();
				}
    		} else {
    			result.put(player.getName(), getValue(data, player.getUniqueId()));
    		}
    	}
        return result;
    }
    
    public OfflinePlayer[] tidyList(OfflinePlayer[] list, String data){
    	boolean permut = true;
    	while(permut){
    		permut = false;
    		for(int i = 0; i < (list.length - 1); i++){
	    		int data1 = getValue(data, list[i].getUniqueId());
	    		int data2 = getValue(data, list[i + 1].getUniqueId());
	    		if(data1 > data2){
	    			OfflinePlayer temp = Bukkit.getOfflinePlayer(list[i].getUniqueId());
	    			list[i] = Bukkit.getOfflinePlayer(list[i + 1].getUniqueId());
	    			list[i + 1] = temp;
	    			permut = true;
	    		}
    		}
    	}
    	return list;
    }

    public Map<String, Integer> getPlayersInRange(String timer, int minimum, int maximum) {
        return new HashMap<String, Integer>();
    }

    public void verifyFormat() {
        if (this.plugin.firstRun()) {
            ConfigurationSection section = this.yaml.getFile().getConfigurationSection("users");
            if(section != null){
	            for (String s : section.getKeys(false)) {
	                this.yaml.getFile().set("users." + s + ".onlinetime", 0);
	            }
	            this.yaml.forceSave();
            }
        }
    }

    public void init() {
        this.yaml = new YAML();
    }

    public void startRunnables() {
        this.plugin.getExecutiveManager().runAsyncTaskRepeat(new AddRunnable(this.plugin), 60L, 60L);
    }
    
    public void startConversion(String newType, String... players) {
        this.plugin.onDisable();
        this.plugin.getExecutiveManager().runAsyncTask(new StartConvertRunnable(this.plugin, newType, players), 0L);
    }

    public void cleanup() {
        this.yaml.forceSave();
        this.yaml = null;
    }
    
    /**
     * Be realy careful on using this !
     *
 	* @since 1.4.6
 	* @version 1.4.6
     */
    public YAML getYaml(){
    	return this.yaml;
    }
}
