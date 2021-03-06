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

import me.boomboompower.uhcplugin.utils.EnumChatFormatting;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {

    private ItemStack stackIn;

    public ItemBuilder(Material item) {
        this(new ItemStack(item));
    }

    public ItemBuilder(ItemStack stackIn) {
        this.stackIn = stackIn;
    }

    public void setName(String name) {
        stackIn.getItemMeta().setDisplayName(EnumChatFormatting.translateAlternateColorCodes('&', name));
    }

    public String getName() {
        return stackIn.getItemMeta().hasDisplayName() ? stackIn.getItemMeta().getDisplayName() : stackIn.getType().name();
    }

    public void setLore(String... lines) {
        List<String> lineList = new ArrayList<>();
        lineList.addAll(Arrays.asList(lines));
        stackIn.getItemMeta().setLore(lineList);
    }

    public List<String> getLore() {
        return stackIn.getItemMeta().hasLore() ? stackIn.getItemMeta().getLore() : new ArrayList<>();
    }

    public ItemStack getItem() {
        return this.stackIn;
    }
}
