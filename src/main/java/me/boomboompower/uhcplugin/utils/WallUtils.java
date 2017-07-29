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
import me.boomboompower.uhcplugin.events.GenerationCompleteEvent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class WallUtils {

    public static void addBorder(final String worldName, final Material material, final int radius) {
        new BukkitRunnable() {

            private int counter = -radius - 1;
            private boolean phase1 = false;
            private boolean phase2 = false;
            private boolean phase3 = false;

            @Override
            public void run() {
                if (!phase1) {
                    int maxCounter = counter + 500;
                    int x = -radius - 1;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
                        if (radius == 100) {
                            block.setType(Material.GLASS);
                        } else {
                            block.setType(material);
                        }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase1 = true;
                    }

                    return;
                }

                if (!phase2) {
                    int maxCounter = counter + 500;
                    for (int z = counter; z <= radius && counter <= maxCounter; z++, counter++) {
                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(radius, z);
                        if (radius == 100) {
                            block.setType(Material.GLASS);
                        } else {
                            block.setType(material);
                        }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase2 = true;
                    }

                    return;
                }

                if (!phase3) {
                    int maxCounter = counter + 500;
                    int z = -radius - 1;
                    for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                        if (x == radius || x == -radius - 1) {
                            continue;
                        }

                        Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, z);
                        if (radius == 100) {
                            block.setType(Material.GLASS);
                        } else {
                            block.setType(material);
                        }
                    }

                    if (counter >= radius) {
                        counter = -radius - 1;
                        phase3 = true;
                    }
                    return;
                }


                int maxCounter = counter + 500;
                for (int x = counter; x <= radius && counter <= maxCounter; x++, counter++) {
                    if (x == radius || x == -radius - 1) {
                        continue;
                    }

                    Block block = Bukkit.getServer().getWorld(worldName).getHighestBlockAt(x, radius);
                    if (radius == 100) {
                        block.setType(Material.GLASS);
                    } else {
                        block.setType(material);
                    }
                }

                if (counter >= radius) {
                    GenerationCompleteEvent event = new GenerationCompleteEvent(Bukkit.getWorld(worldName), radius);
                    Bukkit.getPluginManager().callEvent(event);

                    this.cancel();
                }
            }
        }.runTaskTimer(UHCPlugin.getInstance(), 0, 5);
    }
}
