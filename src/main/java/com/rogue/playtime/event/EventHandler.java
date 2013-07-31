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

import static com.rogue.playtime.Playtime._;
import com.rogue.playtime.Playtime;
import com.rogue.playtime.runnable.EventRunnable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitTask;

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
    private Map<String, BukkitTask> events = new HashMap();
    private Map<String, Event> yamlEvents = new HashMap();
    private Map<String, Integer> eventTimes = new HashMap();
    private List<Event> loginEvents = new ArrayList();

    public EventHandler(Playtime p) {
        plugin = p;
        file = new File(plugin.getDataFolder(), "events.yml");
        loadEvents();
    }

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
            loginEvents.add(new Event(name, timer, seconds / 60, commands, yaml.getBoolean("events." + name + ".repeat")));
        } else {
            events.put(name, Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, new EventRunnable(plugin, name, timer, seconds / 60, (seconds + interval) / 60, commands, yaml.getBoolean("events." + name + ".repeat")), interval * 20L, interval * 20L));
        }
    }

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
        eventTimes.put(name, seconds / 60);
        if (login) {
            loginEvents.add(new Event(name, timer, seconds / 60, commands, yaml.getBoolean("events." + name + ".repeat")));
        } else {
            yamlEvents.put(name, new Event(name, timer, seconds / 60, commands, yaml.getBoolean("events." + name + ".repeat")));
        }
    }

    public Map<String, Integer> getTimes() {
        return eventTimes;
    }

    public void fireYAMLEvents(List<String> fire, String username) {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        for (String s : fire) {
            for (String c : yamlEvents.get(s).getCommands()) {
                if (this.isMessage(c)) {
                    Bukkit.getPlayer(username).sendMessage(_("[&ePlaytime&f] &6" + this.replaceMessage(c).replace("%u", username)));
                } else {
                    Bukkit.dispatchCommand(ccs, c.replace("%u", username));
                }
            }
        }
    }
    
    public void fireLoginEvents(String username) {
        ConsoleCommandSender ccs = Bukkit.getConsoleSender();
        for (Event e : loginEvents) {
            for (String c : e.getCommands()) {
                if (this.isMessage(c)) {
                    Bukkit.getPlayer(username).sendMessage(_("[&ePlaytime&f] &6" + this.replaceMessage(c).replace("%u", username)));
                } else {
                    Bukkit.dispatchCommand(ccs, c.replace("%u", username));
                }
            }
        }
    }

    public void cancelChecks() {
        for (String s : events.keySet()) {
            events.get(s).cancel();
        }
    }

    public boolean isMessage(String test) {
        return test.equalsIgnoreCase("msg") || test.equalsIgnoreCase("message") || test.equalsIgnoreCase("whipser") || test.equalsIgnoreCase("w") || test.equalsIgnoreCase("m") || test.equalsIgnoreCase("t") || test.equalsIgnoreCase("tell");
    }

    public String replaceMessage(String test) {
        String[] back = test.split(" ");
        if (back.length >= 3) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i < test.length(); i++) {
                sb.append(back[i]).append(" ");
            }
            return sb.toString().trim();
        }
        return test;
    }
}
