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

package me.boomboompower.uhcplugin.utils;

import me.boomboompower.uhcplugin.UHCPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalUtils {

    public static void sendMessage(CommandSender sender, String message, boolean usePrefix, Object... replacements) {
        sender.sendMessage((usePrefix ? UHCPlugin.PREFIX  : EnumChatFormatting.GRAY + "") + EnumChatFormatting.translateAlternateColorCodes('&', String.format(message, replacements)));
    }

    public static void sendToAll(String message, boolean usePrefix, Object... replacements) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendMessage(player, message, usePrefix, replacements);
        }
    }
}
