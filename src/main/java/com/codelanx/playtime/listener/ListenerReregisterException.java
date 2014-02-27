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
package com.codelanx.playtime.listener;

/**
 * Exception thrown when a listener is attempted to be registered under the same key
 *
 * @since 2.0.0
 * @author 1Rogue
 * @version 2.0.0
 */
public class ListenerReregisterException extends RuntimeException {
    
    /**
     * The exception constructor
     * 
     * @since 2.0.0
     * @version 2.0.0
     * 
     * @param message The message to convey
     */
    public ListenerReregisterException(String message) {
        super(message);
    }

}
