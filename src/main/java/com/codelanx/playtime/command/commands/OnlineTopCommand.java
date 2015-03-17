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
package main.java.com.codelanx.playtime.command.commands;

import java.util.Map;

import main.java.com.codelanx.playtime.Playtime;
import main.java.com.codelanx.playtime.command.CommandBase;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 *
 * @since 1.4.0
 * @author 1Rogue
 * @version 1.5.0
 */
public class OnlineTopCommand implements CommandBase {
    
    private final Playtime plugin;
    
    public OnlineTopCommand(Playtime plugin) {
        this.plugin = plugin;
    }

	public boolean execute(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        boolean scoreboard = false;
        if (sender instanceof Player) {
            scoreboard = true;
        }
        byte i = 5;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("clear") && scoreboard) {
                Player p = (Player)sender;
                p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                return true;
            }
            try {
                int temp = Integer.parseInt(args[0]);
                if (temp < 1) {
                    temp = 1;
                }
                if (temp > 10) {
                    temp = 10;
                }
                i = Byte.parseByte(temp + "");
            } catch (NumberFormatException e) {}
        }
        Map<String, Integer> players = this.plugin.getDataManager().getDataHandler().getTopPlayers("onlinetime", i);
        if (players == null) {
            sender.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.onlinetop.disabled-flatfile")));
            return true;
        }
        if (scoreboard) {
            Player p = (Player)sender;
            ScoreboardManager sbm = Bukkit.getScoreboardManager();
            Scoreboard scoreBoard = sbm.getNewScoreboard();
            Objective objv = scoreBoard.registerNewObjective("onlinetop", "dummy");
            objv.setDisplaySlot(DisplaySlot.SIDEBAR);
            objv.setDisplayName(this.plugin.getCipher().getString("command.commands.onlinetop.title-shown"));
            Score score;
            int count = 0;
            for (String s : players.keySet()) {
            	int time = players.get(s);
            	int hours = time / 60;
            	int minutes = time % 60;
                score = objv.getScore(ChatColor.GREEN + s + ": " + ChatColor.GOLD + hours + "h" + minutes + "min");
                score.setScore(count);
                count++;
            }
            p.setScoreboard(scoreBoard);
            p.sendMessage(Playtime.__(this.plugin.getCipher().getString("command.commands.onlinetop.clear")));
            
        } else {
            StringBuilder sb = new StringBuilder(this.plugin.getCipher().getString("command.commands.onlinetop.console-title", i));
            for (String s : players.keySet()) {
                sb.append('\n').append(s).append(" - ").append(players.get(s)/60);
            }
            sender.sendMessage(sb.toString());
        }
        return true;
    }

    public String getName() {
        return "onlinetimetop";
    }

}