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
package com.codelanx.playtime.runnable;

import com.codelanx.playtime.Playtime;
import com.codelanx.playtime.player.PlaytimePlayer;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @since 1.2.0
 * @author 1Rogue
 * @version 1.4.0
 */
public class AFKRunnable implements Runnable {

    private final Playtime plugin;

    public AFKRunnable(Playtime plugin) {
        this.plugin = plugin;
    }

    public void run() {
        Map<String, PlaytimePlayer> players = this.plugin.getPlayerHandler().getPlayers();
        if (Bukkit.getOnlinePlayers().length > 0) {
            if (this.plugin.getDebug() >= 1) {
                this.plugin.getLogger().info(this.plugin.getCipher().getString("runnable.afk.check"));
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!players.get(p.getName().toLowerCase()).isAFK() && p.getLocation().equals(players.get(p.getName().toLowerCase()).getSavedLocation())) {
                    this.plugin.getPlayerHandler().incrementTime(p.getName());
                    if (this.plugin.getDebug() >= 3) {
                        this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.afk.time", p.getName()));
                    }
                } else if (!players.get(p.getName().toLowerCase()).isAFK()) {
                    this.plugin.getPlayerHandler().updatePlayer(p.getName(), p.getLocation());
                    if (this.plugin.getDebug() >= 2) {
                        this.plugin.getLogger().log(Level.INFO, this.plugin.getCipher().getString("runnable.afk.location", p.getName()));
                    }
                }
            }
        }
    }
}
