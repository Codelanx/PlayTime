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
import static com.rogue.playtime.Playtime._;
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
                        plugin.getDataManager().convertData(converters.get(sender));
                        converters.remove(sender);
                        sender.sendMessage(_("[&ePlaytime&f] &6Please note that this may take up to 1 minute to complete, depending on the size of your database."));
                        plugin.setBusy(true);
                    }
                } else if (args[0].equalsIgnoreCase("cancel")) {
                    if (converters.get(sender) != null) {
                        converters.remove(sender);
                    }
                }
                break;
            case 2:
                if (args[0].equalsIgnoreCase("convert") && sender.hasPermission("playtime.convert")) {
                    args[1] = args[1].toLowerCase();
                    if (args[1].equals("mysql") || args[1].equals("sqlite")) {
                        converters.put(sender, args[1]);
                        sender.sendMessage(_("[&ePlaytime&f] &6Converting from data type '" + plugin.getDataManager().getDataHandler().getName() + "' to " + args[1] + "!"));
                        sender.sendMessage(_("[&ePlaytime&f] &6You will need to either confirm this action or cancel it using &e/pt confirm &6or &e/pt cancel"));
                    } else if (args[1].equals("flatfile")) {
                        sender.sendMessage(_("[&ePlaytime&f] &6Converting data to flat files is not allowed!"));
                    } else {
                        sender.sendMessage(_("[&ePlaytime&f] &6Unknown data type '" + args[1] + "'"));
                    }
                }
                break;
            default:
                sender.sendMessage(_("&ePlaytime v" + plugin.getDescription().getVersion() + "&f - &6Developed by 1Rogue"));
                break;
        }
        return false;
    }

    public String getName() {
        return "pt";
    }
}
