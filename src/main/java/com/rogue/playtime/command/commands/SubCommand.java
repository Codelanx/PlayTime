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

import org.bukkit.command.CommandSender;

/**
 *
 * @since 2.0.0
 * @author 1Rogue
 * @version 2.0.0
 */
public interface SubCommand {

    /**
     * Executes a relevant command grabbed from the CommandHandler.
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param sender The command executor
     * @param args The command arguments, starting after the command name
     * 
     * @return true on success, false if failed
     */
    public abstract boolean execute(CommandSender sender, String[] args);
    
    /**
     * Returns the name of the command, used for storing a hashmap of the
     * commands
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @return The command's name
     */
    public abstract String getName();
    
    /**
     * Represents data put out by the help menu, or incorrect usage
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @return 
     */
    public abstract String[] helpInfo();
    
}
