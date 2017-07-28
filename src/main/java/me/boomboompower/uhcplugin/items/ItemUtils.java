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

package me.boomboompower.uhcplugin.items;

import me.boomboompower.uhcplugin.listeners.SpectatorListener;

import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ItemUtils {

    public static void dropItem(World world, ItemStack stack, int x, int y, int z) {
        world.dropItemNaturally(new Location(world, x, y, z), stack);
    }

    public static int getDropCount(Material material) {
        switch (material) {
            case LAPIS_ORE:
                return 4 + new Random().nextInt(5);
            default:
                return 1;
        }
    }

    public static void setHead(Location loc, String name) {
        loc.getBlock().setType(Material.SKULL);
        loc.getBlock().setData((byte) 3);
        BlockState state = loc.getBlock().getState();
        if (state instanceof Skull) {
            Skull skull = (Skull) state;

            skull.setRotation(BlockFace.EAST);
            skull.setSkullType(SkullType.PLAYER);
            skull.setOwner(name);
        }
        state.update(true, false);
    }

    public static ItemStack getHeadItem(String username) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte) 3);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(username);
        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack[] fromContents(List<ItemStack> stacks) {
        final int[] beginning = {0};
        final ItemStack[][] items = {new ItemStack[]{}};
        stacks.forEach(item -> {
            items[0][beginning[0]] = item;
            beginning[0]++;
        });
        return items[0];
    }

    public static List<ItemStack> toList(ItemStack... items) {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.addAll(Arrays.asList(items));
        return stacks;
    }

    public static class Items {

        public static ItemStack getPlayerTracker() {
            ItemStack tracker = new ItemStack(Material.COMPASS, 1);
            ItemMeta meta = tracker.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Player tracker");
            tracker.setItemMeta(meta);
            return tracker;
        }

        public static ItemStack getGoldenHead() {
            ItemStack stack = new ItemStack(Material.GOLDEN_APPLE, 1);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Golden head");
            meta.setLore(Arrays.asList("It is said that consuming the blood of", "your enemies strengthens the soul..."));
            stack.setItemMeta(meta);
            return stack;
        }
    }

    public static class Inventories {

        public static Inventory getSpectatorInventory() {
            Inventory inv = Bukkit.createInventory(null, 27, "Player Spectator");
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!SpectatorListener.isSpectator(player)) {
                    inv.addItem(getHeadItem(player.getName()));
                }
            }

            if (inv.getContents().length == 0) {
                inv.setItem(13, new ItemStack(Material.WOOL));
            }
            return inv;
        }
    }
}
