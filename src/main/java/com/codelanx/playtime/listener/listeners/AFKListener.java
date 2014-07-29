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
package com.codelanx.playtime.listener.listeners;

import com.codelanx.playtime.Playtime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Actions to take if the AFK management system is enabled
 *
 * @since 1.4.1
 * @author 1Rogue
 * @version 1.4.1
 */
public class AFKListener implements Listener {

    private final Playtime plugin;
    private final boolean chat;

    public AFKListener(Playtime plugin, boolean chat) {
        this.plugin = plugin;
        this.chat = chat;
    }

    /**
     * Registers the player with the AFK checker
     *
     * @since 1.2.0
     * @versino 1.4.1
     *
     * @param e The join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.plugin.getPlayerHandler().putPlayer(e.getPlayer().getName(), 0, e.getPlayer().getLocation());
    }

    /**
     * Removes the player from the AFK checker
     *
     * @since 1.2.0
     * @version 1.4.1
     *
     * @param e The quit event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.plugin.getPlayerHandler().remPlayer(e.getPlayer().getName());
    }

    /**
     * Sets a player as "not AFK" when they move.
     *
     * @since 1.2.0
     * @version 1.4.1
     *
     * @param e The move event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent e) {
        this.plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
    }

    /**
     * Sets a player as "not AFK" when they interact with something.
     *
     * @since 1.2.0
     * @version 1.4.1
     *
     * @param e The interact event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent e) {
        this.plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
    }

    /**
     * Sets a player as "not AFK" when they chat, if enabled.
     *
     * @since 1.4.1
     * @version 1.4.1
     *
     * @param e Asynchronous player chat event
     */
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        if (this.chat) {
            this.plugin.getPlayerHandler().updatePlayer(e.getPlayer().getName(), false);
        }
    }
}
