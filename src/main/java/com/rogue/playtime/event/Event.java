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
    
    public Event(String cname, String timer, Integer trigger, List<String> commands, boolean repeated) {
        name = cname;
        type = timer;
        hours = trigger;
        run = commands;
        repeat = repeated;
        
    }
    
    public String getName() {
        return name;
    }
    
    public String getType() {
        return type;
    }
    
    public Integer getTrigger() {
        return hours;
    }
    
    public List<String> getCommands() {
        return run;
    }
    
    public boolean isRepeated() {
        return repeat;
    }

}
