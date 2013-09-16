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
package com.rogue.playtime.executables;

import com.rogue.playtime.Playtime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Wink wink. I'm so punny
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class ExecutiveManager {
    
    private Playtime plugin;
    private ScheduledExecutorService es;
    private List<ScheduledFuture<?>> executives = new ArrayList();
    
    public ExecutiveManager(Playtime plugin) {
        this.plugin = plugin;
        this.es = Executors.newScheduledThreadPool(10);
    }
    
    /**
     * Runs a repeating asynchronous task under Playtime
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param r The runnable to execute
     * @param startAfter Time (in seconds) to wait before execution
     * @param delay Time (in seconds) between execution to wait
     */
    public void runAsyncTaskRepeat(Runnable r, long startAfter, long delay) {
        this.executives.add(this.es.scheduleWithFixedDelay(r, startAfter, delay, TimeUnit.SECONDS));
    }
    
    /**
     * Runs a single asynchronous task under Playtime
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param r The runnable to execute
     * @param delay Time (in seconds) to wait before execution
     */
    public void runAsyncTask(Runnable r, long delay) {
        this.executives.add(this.es.schedule(r, delay, TimeUnit.SECONDS));
    }
    
    /**
     * Runs a Callable
     * 
     * @since 1.4.0
     * @version 1.4.0
     * 
     * @param c The callable to execute
     * @param delay Time (in seconds) to wait before execution
     */
    public void runCallable(Callable<?> c, long delay) {
        this.es.schedule(c, delay, TimeUnit.SECONDS);
    }
    
    /**
     * Cancels all running tasks/threads under Playtime.
     * 
     * @since 1.4.0
     * @version 1.4.0
     */
    public void cancelAllTasks() {
        for (ScheduledFuture<?> s : this.executives) {
            s.cancel(false);
        }
    }

}
