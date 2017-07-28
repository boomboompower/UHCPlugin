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

package me.boomboompower.uhcplugin.listeners;

import me.boomboompower.uhcplugin.UHCPlugin;
import me.boomboompower.uhcplugin.configuration.StatisticsConfiguration;
import me.boomboompower.uhcplugin.events.GameStartEvent;
import me.boomboompower.uhcplugin.items.ItemUtils;
import me.boomboompower.uhcplugin.utils.EnumChatFormatting;
import me.boomboompower.uhcplugin.utils.GlobalUtils;
import me.boomboompower.uhcplugin.utils.Permissions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerListener implements Listener {

    private static PlayerListener instance;

    private ArrayList<Player> pearled = new ArrayList<>();

    private HashMap<Player, Integer> kills = new HashMap<>();

    public int maxPlayerCount = 24;
    public int minPlayerCount = 12;

    public PlayerListener() {
        instance = this;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (UHCPlugin.hasStarted()) {
            SpectatorListener.getInstance().setSpectator(player, true);
            GlobalUtils.sendMessage(event.getPlayer(), EnumChatFormatting.RED + "Game has already started, you are a spectator!", true);

            event.setJoinMessage("");
        } else {
            Permissions.setPlayer(player);
            if (getCurrentPlayerCount() >= maxPlayerCount && !Permissions.hasPermission("uhc.join")) {
                player.kickPlayer(EnumChatFormatting.RED + "Server is full, try another game!");
                return;
            }

            event.setJoinMessage(EnumChatFormatting.translateAlternateColorCodes('&', String.format("&e%s has joined (&b%s&e/&b%s&e)", player.getName(), getCurrentPlayerCount(), maxPlayerCount)));

            player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(14);

            SpectatorListener.getInstance().setSpectator(player, SpectatorListener.isSpectator(player));
            for (String playerName : SpectatorListener.getInstance().getDeathList()) {
                player.hidePlayer(Bukkit.getPlayer(playerName));
            }

            ScoreboardManager manager = Bukkit.getScoreboardManager();
            Scoreboard board = manager.getNewScoreboard();

            Objective objective = board.registerNewObjective("showhealth", "health");
            objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objective.setDisplayName("/ 20");

            new StatisticsConfiguration(player).load();

            for (Player online : Bukkit.getOnlinePlayers()) {
                online.setScoreboard(board);
                online.setHealth(online.getHealth());
            }

            if (kills.containsKey(player)) {
                updateScoreboard(player, false);
            }

            if (getCurrentPlayerCount() >= minPlayerCount && !UHCPlugin.hasStarted() && UHCPlugin.getInstance().currentGamestate != UHCPlugin.State.STARTING) {

                // Call our event to start the game
                GameStartEvent gameStartEvent = new GameStartEvent();
                Bukkit.getPluginManager().callEvent(gameStartEvent);
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (SpectatorListener.isSpectator(event.getPlayer())) {
            event.setQuitMessage("");
        } else {
            event.setQuitMessage(EnumChatFormatting.translateAlternateColorCodes('&', String.format("&e%s has quit!", event.getPlayer().getName())));

            if (getCurrentPlayerCount() > minPlayerCount && UHCPlugin.getInstance().currentGamestate == UHCPlugin.State.STARTING) {
                Bukkit.getScheduler().cancelTasks(UHCPlugin.getInstance());
                GlobalUtils.sendToAll(EnumChatFormatting.RED + "Start cancelled, not enough players!", false);
            }
        }
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if (!SpectatorListener.isSpectator(event.getPlayer())) {
            event.setFormat(EnumChatFormatting.GRAY + "%s: %s");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player death = event.getEntity();
        Location location = event.getEntity().getLocation();

        death.getWorld().strikeLightningEffect(location);

        death.setHealth(20);
        death.setFoodLevel(20);
        death.setMaxHealth(20);
        SpectatorListener.getInstance().setSpectator(death, true);
        updateScoreboard(death, true);

        event.setDeathMessage(String.format(EnumChatFormatting.RED + "%s died!", death.getName()));

        if (UHCPlugin.getInstance().useTimeBomb) {
            event.setDeathMessage(null);
            location.getBlock().setType(Material.CHEST);
            new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1).getBlock().setType(Material.CHEST);
            Chest chest = (Chest) location.getBlock().getState();
            chest.getBlockInventory().setContents(ItemUtils.fromContents(event.getDrops()));
            event.getDrops().clear();
            chest.update(true, false);

            GlobalUtils.sendToAll("%s\'s corpse is exploding in 30 seconds!", true, death.getName());

            Bukkit.getScheduler().scheduleSyncDelayedTask(UHCPlugin.getInstance(), new BukkitRunnable() {
                @Override
                public void run() {
                    location.getBlock().setType(Material.AIR);
                    new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1).getBlock().setType(Material.AIR);
                    ((TNTPrimed) death.getWorld().spawnEntity(location, EntityType.PRIMED_TNT)).setFuseTicks(0); // Use this so blocks don't break

                    GlobalUtils.sendToAll("%s\'s corpse exploded!", true, death.getName());
                }
            }, 300);
        } else if (UHCPlugin.getInstance().dropHeads) {
            death.getWorld().getBlockAt(location).setType(Material.NETHER_FENCE);
            ItemUtils.setHead(location.add(0, 1, 0), death.getName());

            event.setKeepInventory(false);
            event.setKeepLevel(false);
        }

        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() != death) {
            Player killer = event.getEntity().getKiller();

            event.setDeathMessage(String.format(EnumChatFormatting.RED + "%s was slain by %s", death.getName(), killer.getName()));

            if (kills.containsKey(killer)) {
                kills.put(killer, kills.get(killer) + 1);
            } else {
                kills.put(killer, 1);
            }
            updateScoreboard(killer, false);
        }
        event.getDrops().remove(ItemUtils.Items.getPlayerTracker());
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().equals(ItemUtils.Items.getGoldenHead())) {
            player.getActivePotionEffects().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
            GlobalUtils.sendMessage(player, EnumChatFormatting.GREEN + "You now have regeneration 2 for 10 seconds!", false);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!UHCPlugin.getInstance().usePearlDamage && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getTo());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getItem() != null && event.getItem().getType() == Material.SKULL_ITEM && event.getItem().getData().getData() == 3) {
            event.setCancelled(true);
            event.getPlayer().getInventory().remove(event.getItem());
            event.getPlayer().addPotionEffect(PotionEffectType.REGENERATION.createEffect(5000, 2), true);
            GlobalUtils.sendMessage(event.getPlayer(), EnumChatFormatting.GREEN + "You ate a player head and gained 5 seconds of regeneration III", false);
        }
    }

    private void updateScoreboard(Player player, boolean remove) {
        if (!remove) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(EnumChatFormatting.translateAlternateColorCodes('&', "&b&lUHC"));

            Score score = objective.getScore(EnumChatFormatting.GREEN + "Kills:");
            score.setScore(kills.get(player));

            player.setScoreboard(scoreboard);
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }

    private int getCurrentPlayerCount() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!SpectatorListener.isSpectator(player)) {
                count++;
            }
        }
        return count;
    }

    public HashMap<Player, Integer> getKills() {
        return kills;
    }

    public static PlayerListener getInstance() {
        return instance;
    }
}
