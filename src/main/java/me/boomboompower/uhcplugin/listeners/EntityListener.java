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
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (UHCPlugin.getInstance().useCutclean) {
            for (int i = 0; i < event.getDrops().size(); i++) {
                switch (event.getDrops().get(i).getType()) {
                    case RAW_CHICKEN:
                        event.getDrops().set(i, new ItemStack(Material.COOKED_CHICKEN, event.getDrops().get(i).getAmount()));
                        break;
                    case MUTTON:
                        event.getDrops().set(i, new ItemStack(Material.COOKED_MUTTON, event.getDrops().get(i).getAmount()));
                        break;
                    case RAW_BEEF:
                        event.getDrops().set(i, new ItemStack(Material.COOKED_BEEF, event.getDrops().get(i).getAmount()));
                        break;
                    case PORK:
                        event.getDrops().set(i, new ItemStack(Material.GRILLED_PORK, event.getDrops().get(i).getAmount()));
                        break;
                    case RABBIT:
                        event.getDrops().set(i, new ItemStack(Material.COOKED_RABBIT, event.getDrops().get(i).getAmount()));
                        break;
                }
            }
        }
    }
}
