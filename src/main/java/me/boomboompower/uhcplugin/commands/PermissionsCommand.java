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

import me.boomboompower.uhcplugin.utils.EnumChatFormatting;

import me.boomboompower.uhcplugin.utils.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionsCommand implements CommandBase {

    /**
     * Sets the command as the following
     *
     * @return the command that must be executed to trigger onCommand
     */
    @Override
    public String getCommand() {
        return "permission";
    }

    /**
     * Gets the default usage of a command
     *
     * @return the default usage of the command
     */
    @Override
    public String getCommandUsage() {
        return EnumChatFormatting.RED + "Usage: /" + getCommand() + " <player>";
    }

    /**
     * Gets the required permission to use this command
     *
     * @return Required permission to execute the command
     */
    @Override
    public String getRequiredPermission() {
        return "uhc.permissions";
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
                if (Bukkit.getPlayer(args[0]) != null && Bukkit.getPlayer(args[0]).isOnline()) {
                    if (args.length == 1) {
                        sendMessage(sender, EnumChatFormatting.RED + "Specify what permission you wish to check");
                    } else {
                        Permissions.setPlayer(Bukkit.getPlayer(args[0]));
                        if (args.length == 2) {
                            sendMessage(sender, "This player %s this permission!", Permissions.hasPermission(args[1]) ? EnumChatFormatting.GREEN + "has" + EnumChatFormatting.RESET : EnumChatFormatting.RED + "does not have" + EnumChatFormatting.RESET);
                        } else {
                            if (args[2].equalsIgnoreCase("give")) {
                                if (!Permissions.hasPermission(args[1])) {
                                    Permissions.addPermission(args[1]);
                                } else {
                                    sendMessage(sender, EnumChatFormatting.RED + "%s already has this permission!", args[0]);
                                }
                            } else if (args[2].equalsIgnoreCase("take")) {
                                if (Permissions.hasPermission(args[1])) {
                                    Permissions.removePermission(args[1]);
                                } else {
                                    sendMessage(sender, EnumChatFormatting.RED + "%s does not have this permission!", args[0]);
                                }
                            } else {
                                sendMessage(sender, EnumChatFormatting.RED + "Invalid arguments!");
                            }
                        }
                    }
                } else {
                    sendMessage(sender, EnumChatFormatting.RED + "Could not find %s", args[0]);
                }
            }
        } else {
            sendNoPermissionMessage(sender);
        }
        return false;
    }

    @Override
    public boolean canCommandSenderUseCommand(CommandSender sender) {
        return !(sender instanceof Player) && sender.hasPermission(getRequiredPermission());
    }
}
