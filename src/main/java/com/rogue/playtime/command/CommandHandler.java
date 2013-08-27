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

import static com.rogue.playtime.Playtime._;
import com.rogue.playtime.command.commands.*;
import com.rogue.playtime.Playtime;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 * Manages commands abstractly for the plugin
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.4.2
 */
public class CommandHandler implements CommandExecutor {

    protected Playtime plugin;
    private Map<String, CommandBase> commands = new HashMap();

    public CommandHandler(Playtime p) {
        plugin = p;

        PlayCommand playtime = new PlayCommand();
        commands.put(playtime.getName(), playtime);
        DeathCommand death = new DeathCommand();
        commands.put(death.getName(), death);
        OnlineCommand online = new OnlineCommand();
        commands.put(online.getName(), online);
        PlayTopCommand playtop = new PlayTopCommand();
        commands.put(playtop.getName(), playtop);
        DeathTopCommand deathtop = new DeathTopCommand();
        commands.put(deathtop.getName(), deathtop);
        OnlineTopCommand onlinetop = new OnlineTopCommand();
        commands.put(onlinetop.getName(), onlinetop);
        PTCommand pt = new PTCommand();
        commands.put(pt.getName(), pt);
    }

    /**
     * Registers the commands to this command executor.
     *
     * @since 1.3.0
     * @version 1.4.0
     */
    public void registerExecs() {
        plugin.getCommand("playtime").setExecutor(this);
        plugin.getCommand("deathtime").setExecutor(this);
        plugin.getCommand("onlinetime").setExecutor(this);
        plugin.getCommand("playtimetop").setExecutor(this);
        plugin.getCommand("deathtimetop").setExecutor(this);
        plugin.getCommand("onlinetimetop").setExecutor(this);
        plugin.getCommand("pt").setExecutor(this);
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
        if (this.isReload(commandLabel, args) || !plugin.isBusy()) {
            if (commands.containsKey(commandLabel)) {
                return commands.get(commandLabel).execute(sender, cmd, commandLabel, args);
            }
        } else {
            sender.sendMessage(_(plugin.getCipher().getString("command.handler.busy")));
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
        return cmd.equalsIgnoreCase("pt") && args.length > 0 && args[0].equalsIgnoreCase("reload");
    }
}
