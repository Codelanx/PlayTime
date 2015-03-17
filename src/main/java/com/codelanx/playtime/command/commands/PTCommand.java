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

import java.util.HashMap;
import java.util.Map;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.command.CommandBase;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class PTCommand implements CommandBase {
    
    private final Playtime plugin;
    
    public PTCommand(Playtime plugin) {
        this.plugin = plugin;
    }

    private static Map<CommandSender, String> converters = new HashMap<CommandSender, String>();
    private static Map<CommandSender, String> swappers = new HashMap<CommandSender, String>();

    public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("playtime.reload")) {
                    if (sender instanceof Player) {
                        this.plugin.reload(sender.getName());
                        return true;
                    }
                    this.plugin.reload();

                } else if (args[0].equalsIgnoreCase("confirm")) {
                    if (converters.get(sender) != null) {
                        if (sender instanceof Player) {
                            this.plugin.getDataManager().convertData(converters.get(sender), sender.getName());
                        } else {
                            this.plugin.getDataManager().convertData(converters.get(sender));
                        }
                        converters.remove(sender);
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.longtime")));
                        this.plugin.setBusy(true);
                    } else if (swappers.get(sender) != null) {
                        this.plugin.getConfigurationLoader().getConfig().set("data.manager", swappers.get(sender));
                        this.plugin.getConfigurationLoader().saveConfig();
                        this.plugin.setBusy(true);
                        if (sender instanceof Player) {
                            this.plugin.reload(sender.getName());
                        } else {
                            this.plugin.reload();
                        }
                        swappers.remove(sender);
                    }
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (converters.get(sender) != null) {
                        converters.remove(sender);
                    }
                    if (swappers.get(sender) != null) {
                        swappers.remove(sender);
                    }
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("convert") && sender.hasPermission("playtime.convert")) {
                    args[1] = args[1].toLowerCase();
                    if (args[1].equals("mysql") || args[1].equals("sqlite")) {
                        if (args[1].equals(this.plugin.getDataManager().getDataHandler().getName())) {
                            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.datainuse")));
                            return true;
                        }
                        converters.put(sender, args[1]);
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.convert", this.plugin.getDataManager().getDataHandler().getName(), args[1])));
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.confirm")));
                    } else if (args[1].equals("flatfile")) {
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.flatfile")));
                    } else {
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.baddata", args[0])));
                    }
                } else if (args[0].equalsIgnoreCase("swap") && sender.hasPermission("playtime.swap")) {
                    args[1] = args[1].toLowerCase();
                    if (args[1].equals("mysql") || args[1].equals("sqlite") || args[1].equals("flatfile")) {
                        if (args[1].equals(this.plugin.getDataManager().getDataHandler().getName())) {
                            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.datainuse")));
                            return true;
                        }
                        swappers.put(sender, args[1]);
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.swap", this.plugin.getDataManager().getDataHandler().getName(), args[1])));
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.confirm")));
                    } else {
                        sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.baddata", args[0])));
                    }
                }
                break;
            default:
                sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.pt.version", this.plugin.getDescription().getVersion())));
                break;
        }
        return false;
    }

    public String getName() {
        return "pt";
    }
}
