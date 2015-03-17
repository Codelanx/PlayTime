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
package main.java.com.codelanx.playtime.command.commands;

import java.util.UUID;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.callable.UUIDFetcher;
import main.java.com.codelanx.playtime.command.CommandBase;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.5.0
 */
public class DeathCommand implements CommandBase {
    
    private final Playtime plugin;
    
    public DeathCommand(Playtime plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String name = null;
        UUID check = null;
        String perm = "playtime.death";
        if (args.length == 0 && sender instanceof Player) {
        	if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
        		try {
					check = UUIDFetcher.getUUIDOf(((Player) sender).getName());
				} catch (Exception e) {
					e.printStackTrace();
				}
        	} else {
        		check = ((Player) sender).getUniqueId();
        	}
        	name = sender.getName();
        } else if (args.length == 1) {
            OfflinePlayer o = this.plugin.getServer().getOfflinePlayer(args[0]);
            if (o != null) {
            	if(Bukkit.getMaxPlayers() < this.plugin.maxplayersuuid){
            		try {
    					check = UUIDFetcher.getUUIDOf(o.getName());
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
            	} else {
            		check = o.getUniqueId();
            	}
                name = o.getName();
            }
            perm += ".others";
        } else {
            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.death.console")));
            return true;
        }
        if (sender.hasPermission(perm)) {
            if (this.plugin.getConfigurationLoader().getBoolean("check.death-time")) {
                int time = this.plugin.getDataManager().getDataHandler().getValue("deathtime", check);
                int minutes = time % 60;
                if (time >= 60) {
                    int hours = time / 60;
                    sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.death.playtime-hours", name, hours, (hours == 1 ? "" : "s"), minutes, (minutes == 1 ? "" : "s"))));
                } else {
                    sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.death.playtime-minutes", name, minutes, (minutes == 1 ? "" : "s"))));
                }
            } else {
                sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.death.disabled")));
            }

        } else {
            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.death.noperm")));
        }
        return false;
    }

    @Override
    public String getName() {
        return "deathtime";
    }
}
