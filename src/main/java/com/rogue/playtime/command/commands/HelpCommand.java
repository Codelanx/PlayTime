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
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import org.bukkit.command.CommandSender;

/**
 * Displays help information
 *
 * @since 2.0.0
 * @author 1Rogue
 * @version 2.0.0
 */
public class HelpCommand implements SubCommand {
    
    private final Playtime plugin;
    private final String usagePrefix = "";
    private final String infoPrefix = "";
    
    public HelpCommand(Playtime plugin) {
        this.plugin = plugin;
    }

    public boolean execute(CommandSender sender, String[] args) {
        
        int factor = 5;
        int page = 1;
        
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        
        Collection<SubCommand> subs = this.plugin.getCommandHandler().getCommands();
        SubCommand[] cmds = subs.toArray(new SubCommand[subs.size()]);
        Arrays.sort(cmds, new Comparator<SubCommand>() {

            public int compare(SubCommand o1, SubCommand o2) {
                return o1.getName().compareTo(o2.getName());
            }
            
        });
        
        if (page * factor > cmds.length) {
            
        }
        
        StringBuilder sb = new StringBuilder();
        for (SubCommand cmd : this.plugin.getCommandHandler().getCommands()) {
            sb.append(cmd.helpInfo()[0]).append(" - ").append(cmd.helpInfo()[1]).append('\n');
        }
        
        return true;
    }

    public String getName() {
        return "help";
    }

    public String[] helpInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
