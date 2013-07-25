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

import com.rogue.playtime.Playtime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public class CommandHandler implements CommandExecutor {

    protected Playtime plugin;
    private Map<String, CommandBase> commands = new HashMap();

    public CommandHandler(Playtime p) {
        plugin = p;

        PlaytimeCommand playtime = new PlaytimeCommand();
        commands.put(playtime.getName(), playtime);
        DeathCommand death = new DeathCommand();
        commands.put(death.getName(), death);
    }

    /**
     * Registers the commands to this command executor.
     *
     * @since 1.3.0
     * @version 1.3.0
     */
    public void registerExecs() {
        plugin.getCommand("playtime").setExecutor(this);
        plugin.getCommand("deathtime").setExecutor(this);
    }

    /**
     * Executes the proper command within Playtime
     *
     * @param sender The command executor
     * @param cmd The command instance
     * @param commandLabel The command name
     * @param args The command arguments
     *
     * @return Success of command, false if no command is found
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (plugin.getDebug() >= 1) {
            plugin.getLogger().log(Level.INFO, "onCommand called! commandLabel = {0}", commandLabel);
            if (plugin.getDebug() >= 2) {
                for (String s : commands.keySet()) {
                    plugin.getLogger().log(Level.INFO, "Command key found: {0}", s);
                }
            }
        }
        if (commands.containsKey(commandLabel)) {
            if (plugin.getDebug() >= 1) {
                plugin.getLogger().info("Executing command!");
            }
            return commands.get(commandLabel).execute(sender, cmd, commandLabel, args);
        }
        return false;
    }
}
