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

import me.boomboompower.uhcplugin.utils.GlobalUtils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Inspired by Forge's ICommand class
 *
 * @author boomboompower
 */
public interface CommandBase extends CommandExecutor {

    /**
     * Sets the command as the following
     *
     * @return the command that must be executed to trigger onCommand
     */
    public String getCommand();

    /**
     * Gets the default usage of a command
     *
     * @return the default usage of the command
     */
    public String getCommandUsage();

    /**
     * Gets the required permission to use this command
     *
     * @return Required permission to execute the command
     */
    public String getRequiredPermission();

    /**
     * Executes the given command, returning its success
     *
     * @param sender Source of the command
     * @param command Command which was executed
     * @param args Passed command arguments
     * @return true if a valid command, otherwise false
     */
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public default boolean canCommandSenderUseCommand(CommandSender sender) {
        return sender.hasPermission(getRequiredPermission());
    }

    /**
     * Sends a message to the sender
     *
     * @param sender CommandSender to send the message to
     * @param message Message to send to the CommandSender
     */
    public default void sendMessage(CommandSender sender, String message, Object... replacements) {
        GlobalUtils.sendMessage(sender, message, false, replacements);
    }

    /**
     * Sends a no permission message to the sender by using the {@link #sendMessage(CommandSender, String, Object...)} method
     *
     * @param sender Sender to send the message to
     */
    public default void sendNoPermissionMessage(CommandSender sender) {
        sendMessage(sender, ChatColor.RED + "I\'m sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is in error.");
    }
}
