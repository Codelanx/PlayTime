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

import com.rogue.playtime.Playtime;
import com.rogue.playtime.command.CommandBase;
import static com.rogue.playtime.Playtime._;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class OnlineCommand implements CommandBase {
    
    private final Playtime plugin;
    
    public OnlineCommand(Playtime plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String check;
        String perm = "playtime.online";
        if (args.length == 0 && sender instanceof Player) {
            check = sender.getName();
        } else if (args.length == 1) {
            check = this.plugin.getBestPlayer(args[0]);
            perm += ".others";
        } else {
            sender.sendMessage(_(this.plugin.getCipher().getString("command.commands.online.console")));
            return true;
        }
        if (sender.hasPermission(perm)) {
            if (this.plugin.getConfigurationLoader().getBoolean("check.online-time")) {
                int time = this.plugin.getDataManager().getDataHandler().getValue("onlinetime", check);
                int minutes = time % 60;
                if (time >= 60) {
                    int hours = time / 60;
                    sender.sendMessage(_(this.plugin.getCipher().getString("command.commands.online.playtime-hours", check, hours, (hours == 1 ? "" : "s"), minutes, (minutes == 1 ? "" : "s"))));
                } else {
                    sender.sendMessage(_(this.plugin.getCipher().getString("command.commands.online.playtime-minutes", check, minutes, (minutes == 1 ? "" : "s"))));
                }
            } else {
                sender.sendMessage(_(this.plugin.getCipher().getString("command.commands.online.disabled")));
            }

        } else {
            sender.sendMessage(_(this.plugin.getCipher().getString("command.commands.online.noperm")));
        }
        return false;
    }

    public String getName() {
        return "onlinetime";
    }
}
