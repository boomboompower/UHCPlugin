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

import me.boomboompower.uhcplugin.listeners.SpectatorListener;
import me.boomboompower.uhcplugin.listeners.UHCListener;
import me.boomboompower.uhcplugin.UHCPlugin;
import me.boomboompower.uhcplugin.utils.EnumChatFormatting;
import me.boomboompower.uhcplugin.utils.GlobalUtils;
import me.boomboompower.uhcplugin.items.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Allows the user to change *ALL* of the UHC settings from within the game
 *
 * Permission is 'uhc.command', only grant to appropriate users!
 *
 * @author boomboompower
 */
public class UHCCommand implements CommandBase {

    private final String NOW = EnumChatFormatting.GREEN + "now" + EnumChatFormatting.GRAY;
    private final String NOLONGER = EnumChatFormatting.RED + "no longer" + EnumChatFormatting.GRAY;

    private final String ENABLED = EnumChatFormatting.GREEN + "enabled" + EnumChatFormatting.GRAY;
    private final String DISABLED = EnumChatFormatting.RED + "disabled" + EnumChatFormatting.GRAY;

    /**
     * Sets the command as the following
     *
     * @return the command that must be executed to trigger onCommand
     */
    @Override
    public String getCommand() {
        return "uhc";
    }

    /**
     * Gets the default usage of a command
     *
     * @return the default usage of the command
     */
    @Override
    public String getCommandUsage() {
        return EnumChatFormatting.RED + "Usage: /" + getCommand() + " <info, pearls, rates, cutclean, timebomb, projectile>";
    }

    /**
     * Gets the required permission to use this command
     *
     * @return Required permission to execute the command
     */
    @Override
    public String getRequiredPermission() {
        return "uhc.command";
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
                switch (args[0]) {
                    case "info":
                    case "status":
                        sendMessage(sender, "Cutclean is %s", (UHCPlugin.getInstance().useCutclean) ? ENABLED : DISABLED);
                        sendMessage(sender, "Health on rod hit is %s", (UHCPlugin.getInstance().showProjectilePlayerHealth) ? ENABLED : DISABLED);
                        sendMessage(sender, "Time bomb is %s (Delay:%ss)", (UHCPlugin.getInstance().useTimeBomb) ? ENABLED : DISABLED, UHCPlugin.getInstance().timeBombDelay);
                        break;
                    case "projectile":
                    case "projectiles":
                        sendMessage(sender, "Showing health on rod hit is %s enabled!", (UHCPlugin.getInstance().showProjectilePlayerHealth = !UHCPlugin.getInstance().showProjectilePlayerHealth) ? NOW : NOLONGER);
                        break;
                    case "timebomb":
                        sendMessage(sender, "Time bomb is %s enabled!", (UHCPlugin.getInstance().useTimeBomb = !UHCPlugin.getInstance().useTimeBomb) ? NOW : NOLONGER);
                        break;
                    case "cutclean":
                    case "usecutclean":
                        sendMessage(sender, "Cutclean is %s enabled!", (UHCPlugin.getInstance().useCutclean = !UHCPlugin.getInstance().useCutclean) ? NOW : NOLONGER);
                        break;
                    case "pearl":
                    case "pearls":
                    case "enderpearl":
                    case "enderpearls":
                        sendMessage(sender, "Enderpearl damage is %s enabled!", (UHCPlugin.getInstance().usePearlDamage = !UHCPlugin.getInstance().usePearlDamage) ? NOW : NOLONGER);
                        break;
                    case "head":
                    case "heads":
                    case "skull":
                    case "skulls":
                        sendMessage(sender, "Head dropping is %s enabled!", (UHCPlugin.getInstance().dropHeads = !UHCPlugin.getInstance().dropHeads) ? NOW : NOLONGER);
                        break;
                    case "spec":
                    case "spectator":
                        if (args.length == 1) {
                            sendMessage(sender, EnumChatFormatting.RED + "Usage: /uhc spectator <open, switch>");
                        } else {
                            switch (args[1]) {
                                case "open":
                                case "inventory":
                                    if (sender instanceof Player) {
                                        ((Player) sender).openInventory(ItemUtils.Inventories.getSpectatorInventory());
                                        sendMessage(sender, EnumChatFormatting.GREEN + "Spectator inventory opened");
                                    } else {
                                        sendMessage(sender, EnumChatFormatting.RED + "Only a player can use this subcommand!");
                                    }
                                    break;
                                case "switch":
                                case "toggle":
                                    if (args.length == 2) {
                                        sendMessage(sender, "Please specify whom you wish to toggle!");
                                    } else {
                                        Player player = Bukkit.getPlayer(args[2]);
                                        if (!SpectatorListener.isSpectator(player)) {
                                            player.setHealth(0);
                                            sendMessage(sender, "%s is now a spectator!", args[2]);
                                        } else {
                                            sendMessage(sender, "%s is already a spectator!", args[2]);
                                        }
                                    }
                                    break;
                                default:
                                    sendMessage(sender, EnumChatFormatting.RED + "Usage: /uhc spectator <open, switch>");
                                    break;
                            }
                        }
                        break;
                    case "rates":
                    case "flint":
                    case "apple":
                    case "apples":
                        if (args.length == 1) {
                            sendMessage(sender, EnumChatFormatting.RED + "Usage: /uhc rates <flint|apples|famine> <amount>");
                        } else {
                            switch (args[1]) {
                                case "apple":
                                case "apples":
                                    if (args.length == 2) {
                                        sendMessage(sender, "Apple drop rate forcing has been %s!", (UHCPlugin.getInstance().useAppleLootRates = !UHCPlugin.getInstance().useAppleLootRates) ? ENABLED : DISABLED);
                                    } else {
                                        try {
                                            sendMessage(sender, "Apple drop rate is now %s!", (UHCPlugin.getInstance().appleDropChance = Integer.valueOf(args[2])));
                                        } catch (NumberFormatException ex) {
                                            sendMessage(sender, EnumChatFormatting.RED + "Only use numbers please!");
                                        }
                                    }
                                    break;
                                case "flint":
                                    if (args.length == 2) {
                                        sendMessage(sender, "Flint drop rate forcing has been %s!", (UHCPlugin.getInstance().useFlintLootRates = !UHCPlugin.getInstance().useFlintLootRates) ? ENABLED : DISABLED);
                                    } else {
                                        try {
                                            sendMessage(sender, "Flint drop rate is now %s!", (UHCPlugin.getInstance().flintDropChance = Integer.valueOf(args[2])));
                                        } catch (NumberFormatException ex) {
                                            sendMessage(sender, EnumChatFormatting.RED + "Only use numbers please!");
                                        }
                                    }
                                    break;
                                case "famine":
                                    sendMessage(sender, "Apple famine is now %s!", (UHCPlugin.getInstance().useAppleFamine = !UHCPlugin.getInstance().useAppleFamine) ? ENABLED : DISABLED);
                                    break;
                                default:
                                    sendMessage(sender, EnumChatFormatting.RED + "Usage: /uhc rates <flint|apples> <amount>");
                                    break;
                            }
                        }
                        break;
                    case "heal":
                    case "healme":
                        if (sender instanceof Player) {
                            if (args.length == 1) {
                                ((Player) sender).setFoodLevel(20);
                                ((Player) sender).setHealth(((Player) sender).getMaxHealth());
                                sendMessage(sender, "You have been healed!");
                            } else {
                                switch (args[1]) {
                                    case "heart":
                                    case "hearts":
                                    case "health":
                                        ((Player) sender).setHealth(((Player) sender).getMaxHealth());
                                        sendMessage(sender, "Your health has been healed!");
                                        break;
                                    case "food":
                                    case "hunger":
                                        ((Player) sender).setFoodLevel(20);
                                        sendMessage(sender, "You are no longer hungry!");
                                        break;
                                }
                            }
                        } else {
                            sendMessage(sender, "Only players may use this command!");
                        }
                        break;
                    case "bc":
                    case "alert":
                    case "shout":
                    case "announce":
                    case "broadcast":
                        if (args.length == 1) {
                            sendMessage(sender, EnumChatFormatting.RED + "Arguments cannot be empty!");
                        } else {
                            GlobalUtils.sendToAll(EnumChatFormatting.RED + EnumChatFormatting.translateAlternateColorCodes('&', argsToString(args, 1)), true);
                        }
                        break;
                    default:
                        sendMessage(sender, getCommandUsage());
                }
            }
        } else {
            sendNoPermissionMessage(sender);
        }
        return true;
    }

    /**
     * Sends a message to the sender
     *
     * @param sender CommandSender to send the message to
     * @param message Message to send to the CommandSender
     */
    @Override
    public void sendMessage(CommandSender sender, String message, Object... replacements) {
        GlobalUtils.sendMessage(sender, message, true, replacements);
    }

    private String argsToString(String[] args, int starting) {
        StringBuilder builder = new StringBuilder();
        for (int i = starting; i < args.length; i++) {
            builder.append(args[i]);
            builder.append(" ");
        }
        return builder.toString().trim();
    }
}
