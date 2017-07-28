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

import me.boomboompower.uhcplugin.items.ItemUtils;
import me.boomboompower.uhcplugin.utils.PlayerUtils;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class SpectatorListener implements Listener {

    private static SpectatorListener instance;

    public SpectatorListener() {
        instance = this;
    }

    private ArrayList<String> deathList = new ArrayList<>();

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (deathList.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (deathList.contains(player.getName())) {
            if (event.getItem() != null) {
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem().equals(ItemUtils.Items.getPlayerTracker())) {
                        player.openInventory(ItemUtils.Inventories.getSpectatorInventory());
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && deathList.contains(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (deathList.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (deathList.contains(event.getEntity().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(ItemUtils.Inventories.getSpectatorInventory().getTitle()) && event.getCurrentItem() != null) {
            if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                PlayerUtils.teleportTo((Player) event.getWhoClicked(), Bukkit.getPlayer(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwner()));
            }
        }
    }

    public void setSpectator(Player player, boolean spectator) {
        if (spectator) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setInvulnerable(true);
            player.getInventory().setItem(0, ItemUtils.Items.getPlayerTracker());
        } else {
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(false);
            player.setInvulnerable(false);
        }
        for (Player player1 : Bukkit.getOnlinePlayers()) {
            if (spectator) {
                deathList.add(player.getName());
                player1.hidePlayer(player);
            } else {
                deathList.remove(player.getName());
                player1.showPlayer(player);
            }
        }
    }

    public ArrayList<String> getDeathList() {
        return this.deathList;
    }

    public static boolean isSpectator(Player player) {
        return isSpectator(player.getName());
    }

    public static boolean isSpectator(String playerName) {
        return getInstance().deathList.contains(playerName);
    }

    public static SpectatorListener getInstance() {
        return instance;
    }
}
