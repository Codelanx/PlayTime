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
package com.rogue.playtime.event;

import static com.rogue.playtime.Playtime.__;
import com.rogue.playtime.Playtime;
import com.rogue.playtime.runnable.EventRunnable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class EventHandler {

    private Playtime plugin;
    private YamlConfiguration yaml = null;
    private File file;
    private Map<String, Event> events = new HashMap();

    public EventHandler(Playtime p) {
        plugin = p;
        file = new File(plugin.getDataFolder(), "events.yml");
        loadEvents();
    }
    
    /**
     * Loads the file configuration for events and adds the events
     * 
     * @since 1.4.0
     * @version 1.4.0
     */
    private void loadEvents() {
        if (plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!file.exists()) {
            plugin.saveResource("events.yml", true);
            return;
        }
        yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection eventSection = yaml.getConfigurationSection("events");
        int interval = plugin.getConfigurationLoader().getInt("events.interval");
        if (plugin.getDataManager().getDataHandler().getName().equals("flatfile")) {
            for (String s : eventSection.getKeys(false)) {
                evalYAMLEvent(s, yaml.getBoolean("events." + s + ".at-login"));
            }
        } else {
            for (String s : eventSection.getKeys(false)) {
                evalEvent(s, interval, yaml.getBoolean("events." + s + ".at-login"));
            }
        }

    }

    /**
     * Gets an event for an sql manager and adds it accordingly
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param name The event name
     * @param interval The interval at which runnable checks will repeat at in seconds
     * @param login If the event is a login event
     */
    private void evalEvent(String name, int interval, boolean login) {
        String timer = yaml.getString("events." + name + ".type");
        if (!timer.equalsIgnoreCase("deathtime") && !timer.equalsIgnoreCase("onlinetime") && !timer.equalsIgnoreCase("playtime")) {
            timer = "playtime";
        }
        List<String> commands = yaml.getStringList("events." + name + ".commands");
        if (commands.isEmpty()) {
            return;
        }
        Integer seconds = yaml.getInt("events." + name + ".time");
        if (login) {
            events.put(name, new Event(name, timer, seconds / 60, commands, yaml.getBoolean("events." + name + ".repeat"), login));
        } else {
            plugin.getExecutiveManager().runAsyncTaskRepeat(new EventRunnable(plugin, name, timer, seconds / 60, (seconds + interval) / 60, commands, yaml.getBoolean("events." + name + ".repeat")), interval * 60L, interval * 60L);
        }
    }
    
    /**
     * Gets an event for a flatfile manager and adds it accordingly
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param name The event name
     * @param login If the event is a login event
     */
    private void evalYAMLEvent(String name, boolean login) {
        String timer = yaml.getString("events." + name + ".type");
        if (!timer.equalsIgnoreCase("deathtime") && !timer.equalsIgnoreCase("onlinetime") && !timer.equalsIgnoreCase("playtime")) {
            timer = "playtime";
        }
        List<String> commands = yaml.getStringList("events." + name + ".commands");
        if (commands.isEmpty()) {
            return;
        }
        Integer seconds = yaml.getInt("events." + name + ".time");
        boolean repeat = yaml.getBoolean("events." + name + ".repeat");
        events.put(name, new Event(name, timer, seconds / 60, commands, yaml.getBoolean("events." + name + ".repeat"), login));
    }

    /**
     * Gets a list of static (non-runnable) events in use. This will only return
     * login events if you are using an sql manager.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return Map of events in use
     */
    public Map<String, Event> getEvents() {
        return events;
    }

    /**
     * Fires static events manually for a provided user
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param fire A list of names for events to fire
     * @param username The user to fire events for
     */
    public void fireEvents(List<String> fire, String username) {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        for (String s : fire) {
            for (String c : events.get(s).getCommands()) {
                if (this.isMessage(c)) {
                    Bukkit.getPlayer(username).sendMessage(__(this.replaceMessage(c).replace("%u", username).replace("%t", plugin.getDataManager().getDataHandler().getValue(events.get(s).getType(), username) + "")));
                } else {
                    Bukkit.dispatchCommand(ccs, c.replace("%u", username).replace("%t", plugin.getDataManager().getDataHandler().getValue(events.get(s).getType(), username) + ""));
                }
            }
        }
    }
    
    /**
     * Fires login events for a particular user
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param username The user to fire events for
     */
    public void fireLoginEvents(String username) {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        for (Event e : events.values()) {
            if (e.isLoginEvent()) {
                for (String c : e.getCommands()) {
                    if (this.isMessage(c.split(" ")[0])) {
                        Bukkit.getPlayer(username).sendMessage(__(this.replaceMessage(c).replace("%u", username).replace("%t", this.toReadable(plugin.getDataManager().getDataHandler().getValue(e.getType(), username)))));
                    } else {
                        Bukkit.dispatchCommand(ccs, c.replace("%u", username).replace("%t", plugin.getDataManager().getDataHandler().getValue(e.getType(), username) + ""));
                    }
                }
            }
        }
    }

    /**
     * Determines if a command for an event is a messaging command
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param test The commandLabel to test
     * @return true if messaging command, false if not
     */
    public boolean isMessage(String test) {
        return test.equalsIgnoreCase("msg") || test.equalsIgnoreCase("message") || test.equalsIgnoreCase("whipser") || test.equalsIgnoreCase("w") || test.equalsIgnoreCase("m") || test.equalsIgnoreCase("t") || test.equalsIgnoreCase("tell");
    }

    /**
     * Replaces the first two arguments of a command, intended for messaging
     * commands.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param test The raw command to edit
     * @return The message to send to a player
     */
    public String replaceMessage(String test) {
        String[] back = test.split(" ");
        if (back.length >= 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < back.length; i++) {
                sb.append(back[i]).append(" ");
            }
            return sb.toString().trim();
        }
        return test;
    }
    
    /**
     * Returns an estimation of how long SQLite conversion will take based on
     * the number of rows required to add. The converter uses individual INSERT
     * statements for each row, which is unfortunate due to SQLite's limitations
     * on multiple values within inserts or union selects.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param rows The number of rows to evaluate
     * @return The estimated time as a readable string
     */
    public String toReadable(int time) {
        long minutes = time % 60;
        long hours = time / 60;
        return ((hours >= 1) ? ((hours != 1) ? hours + " " + plugin.getCipher().getString("event.hours") + " " : hours + " " + plugin.getCipher().getString("event.hour")) + " " : "") + ((minutes != 1) ? minutes + " " + plugin.getCipher().getString("event.minutes") + "." : minutes + " " + plugin.getCipher().getString("event.minute") + ".");
    }
}
