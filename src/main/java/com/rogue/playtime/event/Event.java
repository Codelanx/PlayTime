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
package com.rogue.playtime.event;

import java.util.List;

/**
 * An Event object used for events with flatfile storage
 * 
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class Event {
    
    private final String name;
    private final String type;
    private final Integer hours;
    private final List<String> run;
    private final boolean repeat;
    private final boolean login;
    
    public Event(String cname, String timer, Integer trigger, List<String> commands, boolean repeated, boolean atLogin) {
        name = cname;
        type = timer;
        hours = trigger;
        run = commands;
        repeat = repeated;
        login = atLogin;
    }
    
    /**
     * Returns the event name
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return Event name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the timer used for the event
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return Event timer type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Returns the time at which the event is fired
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return Event trigger time
     */
    public Integer getTrigger() {
        return hours;
    }
    
    /**
     * Returns a string list of the commands to execute when the event is fired
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return Event commands
     */
    public List<String> getCommands() {
        return run;
    }
    
    /**
     * Whether the event repeats every x iterations of the time trigger
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return If the event repeats
     */
    public boolean isRepeated() {
        return repeat;
    }
    
    /**
     * Whether the event should fire upon logging in. If true, it will not execute elsewhere.
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @return If the event is used upon logging in.
     */
    public boolean isLoginEvent() {
        return login;
    }

}
