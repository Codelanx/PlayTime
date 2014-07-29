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
package com.codelanx.playtime.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @since 1.3.0
 * @author 1Rogue
 * @version 1.3.0
 */
public interface CommandBase {

    /**
     * Executes a relevant command grabbed from the CommandHandler.
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @param sender The command executor
     * @param cmd The Command object
     * @param commandLabel The string name of the command executed
     * @param args The command arguments
     * 
     * @return true on success, false if failed
     */
    public abstract boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args);
    
    /**
     * Returns the name of the command, used for storing a hashmap of the
     * commands
     * 
     * @since 1.3.0
     * @version 1.3.0
     * 
     * @return The command's name
     */
    public abstract String getName();
    
}
