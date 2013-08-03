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
    
    public ExecutiveManager(Playtime p) {
        plugin = p;
        es = Executors.newScheduledThreadPool(10);
    }
    
    public void runAsyncTaskRepeat(Runnable r, long startAfter, long delay) {
        executives.add(es.scheduleWithFixedDelay(r, startAfter, delay, TimeUnit.SECONDS));
    }
    
    public void runAsyncTask(Runnable r, long delay) {
        executives.add(es.schedule(r, delay, TimeUnit.SECONDS));
    }
    
    public void runCallable(Callable<?> c, long delay) {
        es.schedule(c, delay, TimeUnit.SECONDS);
    }
    
    public void cancelAllTasks() {
        for (ScheduledFuture<?> s : executives) {
            s.cancel(false);
        }
    }

}
