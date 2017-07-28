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
import me.boomboompower.uhcplugin.items.ItemUtils;

import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class WorldListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (UHCPlugin.getInstance().useCutclean) {
            if (event.isDropItems()) {
                switch (event.getBlock().getType()) {
                    case IRON_ORE:
                        event.setDropItems(false);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT, ItemUtils.getDropCount(Material.IRON_ORE)));
                        break;
                    case GOLD_ORE:
                        event.setDropItems(false);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT, ItemUtils.getDropCount(Material.GOLD_ORE)));
                        break;
                }
            }
        }
        if (UHCPlugin.getInstance().useAppleLootRates || UHCPlugin.getInstance().useFlintLootRates) {
            if (event.isDropItems()) {
                switch (event.getBlock().getType()) {
                    case LEAVES:
                    case LEAVES_2:
                        event.setDropItems(false);
                        if (event.getPlayer().getInventory().getItemInMainHand().getType() == Material.SHEARS) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.LEAVES, 1, event.getBlock().getData()));
                        }
                        if (new Random().nextInt(100) <= 3) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SAPLING, 1));
                        }
                        if (!UHCPlugin.getInstance().useAppleFamine) {
                            if (new Random().nextInt(UHCPlugin.getInstance().appleDropChance) == 0) {
                                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
                            }
                        }
                        break;
                    case GRAVEL:
                        event.setDropItems(false);
                        if (new Random().nextInt(UHCPlugin.getInstance().flintDropChance) == 0) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
                        } else {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GRAVEL));
                        }
                        break;
                }
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onExplode(EntityExplodeEvent event) {
        if (event.getEntity() instanceof TNTPrimed) {
            event.blockList().clear();
        }
    }
}
