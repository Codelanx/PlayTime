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
package com.rogue.playtime.command.commands;

import com.rogue.playtime.command.CommandBase;
import static com.rogue.playtime.Playtime.__;
import static com.rogue.playtime.command.CommandBase.plugin;
import java.util.HashMap;
import java.util.Map;
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

    private static Map<CommandSender, String> converters = new HashMap();
    private static Map<CommandSender, String> swappers = new HashMap();

    public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        switch (args.length) {
            case 1:
                if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("playtime.reload")) {
                    if (sender instanceof Player) {
                        plugin.reload(sender.getName());
                        return true;
                    }
                    plugin.reload();

                } else if (args[0].equalsIgnoreCase("confirm")) {
                    if (converters.get(sender) != null) {
                        if (sender instanceof Player) {
                            plugin.getDataManager().convertData(converters.get(sender), sender.getName());
                        } else {
                            plugin.getDataManager().convertData(converters.get(sender));
                        }
                        converters.remove(sender);
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.longtime")));
                        plugin.setBusy(true);
                    } else if (swappers.get(sender) != null) {
                        plugin.getConfigurationLoader().getConfig().set("data.manager", swappers.get(sender));
                        plugin.getConfigurationLoader().saveConfig();
                        plugin.setBusy(true);
                        if (sender instanceof Player) {
                            plugin.reload(sender.getName());
                        } else {
                            plugin.reload();
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
                        if (args[1].equals(plugin.getDataManager().getDataHandler().getName())) {
                            sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.datainuse")));
                            return true;
                        }
                        converters.put(sender, args[1]);
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.convert", plugin.getDataManager().getDataHandler().getName(), args[1])));
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.confirm")));
                    } else if (args[1].equals("flatfile")) {
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.flatfile")));
                    } else {
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.baddata", args[0])));
                    }
                } else if (args[0].equalsIgnoreCase("swap") && sender.hasPermission("playtime.swap")) {
                    args[1] = args[1].toLowerCase();
                    if (args[1].equals("mysql") || args[1].equals("sqlite") || args[1].equals("flatfile")) {
                        if (args[1].equals(plugin.getDataManager().getDataHandler().getName())) {
                            sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.datainuse")));
                            return true;
                        }
                        swappers.put(sender, args[1]);
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.swap", plugin.getDataManager().getDataHandler().getName(), args[1])));
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.confirm")));
                    } else {
                        sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.baddata", args[0])));
                    }
                }
                break;
            default:
                sender.sendMessage(__(plugin.getCipher().getString("command.commands.pt.version", plugin.getDescription().getVersion())));
                break;
        }
        return false;
    }

    public String getName() {
        return "pt";
    }
}
