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
package main.java.com.codelanx.playtime.command;

import java.util.HashMap;
import java.util.Map;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.command.commands.DeathCommand;
import main.java.com.codelanx.playtime.command.commands.DeathTopCommand;
import main.java.com.codelanx.playtime.command.commands.OnlineCommand;
import main.java.com.codelanx.playtime.command.commands.OnlineTopCommand;
import main.java.com.codelanx.playtime.command.commands.PTCommand;
import main.java.com.codelanx.playtime.command.commands.PlayCommand;
import main.java.com.codelanx.playtime.command.commands.PlayTopCommand;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Manages commands abstractly for the plugin
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.2
 */
public class CommandHandler implements CommandExecutor {

    private final Playtime plugin;
    private final Map<String, CommandBase> commands = new HashMap<String, CommandBase>();

    public CommandHandler(Playtime plugin) {
        this.plugin = plugin;
        
        CommandBase[] cmds = new CommandBase[] {
            new PlayCommand(this.plugin),
            new DeathCommand(this.plugin),
            new OnlineCommand(this.plugin),
            new PlayTopCommand(this.plugin),
            new DeathTopCommand(this.plugin),
            new OnlineTopCommand(this.plugin),
            new PTCommand(this.plugin)
        };
        
        final CommandHandler chand = this;
        for (CommandBase cmd : cmds) {
            this.commands.put(cmd.getName(), cmd);
            this.plugin.getCommand(cmd.getName()).setExecutor(chand);
        }
    }

    /**
     * Executes the proper command within Playtime
     *
     * @since 1.3.0
     * @version 1.4.2
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
        if (this.isReload(commandLabel, args) || !this.plugin.isBusy()) {
            CommandBase command = this.commands.get(commandLabel);
            if (command != null) {
                return command.execute(sender, cmd, commandLabel, args);
            }
        } else {
            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.handler.busy")));
        }
        return false;
    }

    /**
     * Returns whether or not the command is a reload.
     *
     * Ex. /pt reload
     *
     * @since 1.4.2
     * @version 1.4.2
     *
     * @param cmd The command name
     * @param args The command arguments
     * @return True if reload, false otherwise
     */
    private boolean isReload(String cmd, String[] args) {
        return cmd.equalsIgnoreCase("pt") && args.length == 1 && args[0].equalsIgnoreCase("reload");
    }
}
