/*
 *     Copyright (C) 2017 boomboompower
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.boomboompower.uhcplugin.commands;

import me.boomboompower.uhcplugin.UHCPlugin;
import me.boomboompower.uhcplugin.events.GameStartEvent;
import me.boomboompower.uhcplugin.listeners.PlayerListener;
import me.boomboompower.uhcplugin.utils.EnumChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class GameCommand implements CommandBase {

    /**
     * Sets the command as the following
     *
     * @return the command that must be executed to trigger onCommand
     */
    @Override
    public String getCommand() {
        return "game";
    }

    /**
     * Gets the default usage of a command
     *
     * @return the default usage of the command
     */
    @Override
    public String getCommandUsage() {
        return EnumChatFormatting.RED + "Usage: /" + getCommand() + "<forcestart, min, max>";
    }

    /**
     * Gets the required permission to use this command
     *
     * @return Required permission to execute the command
     */
    @Override
    public String getRequiredPermission() {
        return "uhc.game";
    }

    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (canCommandSenderUseCommand(sender)) {
            if (args.length == 0) {
                sendMessage(sender, getCommandUsage());
            } else {
                if (UHCPlugin.isStarted()) {
                    sendMessage(sender, EnumChatFormatting.RED + "Game is already started!");
                    return true;
                }
                switch (args[0]) {
                    case "min":
                    case "minplayers":
                        if (args.length == 1) {
                            sendMessage(sender, EnumChatFormatting.RED + "Please specify a number!");
                        } else {
                            try {
                                int newMin = Integer.valueOf(args[1]);
                                if (newMin > PlayerListener.getInstance().maxPlayerCount) {
                                    int max = PlayerListener.getInstance().maxPlayerCount;
                                    PlayerListener.getInstance().maxPlayerCount = newMin;
                                    PlayerListener.getInstance().minPlayerCount = max;

                                    sendMessage(sender, "Switched minimum & maximum player counts");
                                } else {
                                    PlayerListener.getInstance().minPlayerCount = newMin;
                                    sendMessage(sender, "Minimum player count was changed!");
                                }
                                sendMessage(sender, "Min = %s, Max = %s", PlayerListener.getInstance().minPlayerCount, PlayerListener.getInstance().maxPlayerCount);
                            } catch (NumberFormatException e) {
                                sendMessage(sender, EnumChatFormatting.RED + "Invalid number: %s", args[1]);
                            }
                        }
                        break;
                    case "max":
                    case "maxplayers":
                        if (args.length == 1) {
                            sendMessage(sender, EnumChatFormatting.RED + "Please specify a number!");
                        } else {
                            try {
                                int newMax = Integer.valueOf(args[1]);
                                if (newMax < PlayerListener.getInstance().minPlayerCount) {
                                    int min = PlayerListener.getInstance().maxPlayerCount;
                                    PlayerListener.getInstance().minPlayerCount = newMax;
                                    PlayerListener.getInstance().maxPlayerCount = min;

                                    sendMessage(sender, "Switched minimum & maximum player counts");
                                } else {
                                    PlayerListener.getInstance().maxPlayerCount = newMax;
                                    sendMessage(sender, "Maximum player count was changed!");
                                }
                                sendMessage(sender, "Min = %s, Max = %s", PlayerListener.getInstance().minPlayerCount, PlayerListener.getInstance().maxPlayerCount);
                            } catch (NumberFormatException e) {
                                sendMessage(sender, EnumChatFormatting.RED + "Invalid number: %s", args[1]);
                            }
                        }
                        break;
                    case "start":
                    case "forcestart":
                        GameStartEvent event = new GameStartEvent();
                        Bukkit.getPluginManager().callEvent(event);
                        break;
                    default:
                        sendMessage(sender, getCommandUsage());
                }
            }
        } else {
            sendNoPermissionMessage(sender);
        }
        return false;
    }
}
