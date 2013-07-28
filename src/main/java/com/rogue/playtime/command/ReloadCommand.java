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
package com.rogue.playtime.command;

import static com.rogue.playtime.command.CommandBase.plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ReloadCommand implements CommandBase {

    public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender.hasPermission("playtime.reload")) {
            if (sender instanceof Player) {
                plugin.reload(sender.getName());
                return true;
            }
            plugin.reload();
        }
        return true;
    }

    public String getName() {
        return "playtimereload";
    }
}
