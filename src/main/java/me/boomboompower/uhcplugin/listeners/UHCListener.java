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
import me.boomboompower.uhcplugin.events.GameStartEvent;
import me.boomboompower.uhcplugin.events.GenerationCompleteEvent;
import me.boomboompower.uhcplugin.utils.GlobalUtils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCListener implements Listener {

    private static UHCListener instance;

    public UHCListener() {
        instance = this;
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        if (!event.isCancelled()) {
            UHCPlugin.getInstance().currentGamestate = UHCPlugin.State.STARTING;

            final int[] seconds = {10};
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (seconds[0] == 0) {
                        Bukkit.getScheduler().cancelTask(getTaskId());
                        UHCPlugin.getInstance().currentGamestate = UHCPlugin.State.STARTED;
                        return;
                    }
                    GlobalUtils.sendToAll("&bGame starting in &9%s&b second(s)!", false, seconds[0]);
                    seconds[0]--;
                }
            }.runTaskTimer(UHCPlugin.getInstance(), 0L, 20L);
        }
    }

    @EventHandler
    public void onGenComplete(GenerationCompleteEvent event) {
        UHCPlugin.log("Generated walls for %s", event.getWorldName());

        event.getWorld().getWorldBorder().setCenter(0, 0);
        event.getWorld().getWorldBorder().setSize(event.getRadius() * 2);

    }

    public static UHCListener instance() {
        return instance;
    }
}
