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

package me.boomboompower.uhcplugin;

import me.boomboompower.uhcplugin.utils.ItemUtils;
import me.boomboompower.uhcplugin.utils.GlobalUtils;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class UHCEvents implements Listener {

    private static UHCEvents instance;

    private JavaPlugin registry;

    private ArrayList<Player> pearled = new ArrayList<>();
    private ArrayList<String> deathList = new ArrayList<>();

    private HashMap<Player, Integer> kills = new HashMap<>();

    public UHCEvents(JavaPlugin plugin) {
        registry = plugin;
        instance = this;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(14);

        setSpectator(event.getPlayer(), deathList.contains(event.getPlayer().getName()));
        for (String playerName : deathList) {
            event.getPlayer().hidePlayer(Bukkit.getPlayer(playerName));
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective objective = board.registerNewObjective("showhealth", "health");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        objective.setDisplayName("/ 20");

        for (Player online : Bukkit.getOnlinePlayers()){
            online.setScoreboard(board);
            online.setHealth(online.getHealth());
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
        setSpectator(death, true);
        updateScoreboard(death, true);

        event.setDeathMessage(String.format(ChatColor.RED + "%s died!", death.getName()));

        if (UHCPlugin.dropHeads) {
            death.getWorld().getBlockAt(location).setType(Material.NETHER_FENCE);
            death.getWorld().getBlockAt(location.add(0, 1, 0)).setType(Material.SKULL);
            ItemUtils.setHead(location.add(0, 1, 0), death.getName());

            event.setKeepInventory(false);
            event.setKeepLevel(false);
        }

        if (UHCPlugin.useTimeBomb) {
            event.setDeathMessage(null);
            location.getBlock().setType(Material.CHEST);
            new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1).getBlock().setType(Material.CHEST);
            Chest chest = (Chest) location.getBlock().getState();
            chest.getInventory().setStorageContents(death.getInventory().getExtraContents());
            event.getDrops().clear();
            chest.update();

            GlobalUtils.sendToAll("%s\'s corpse is exploding in 30 seconds!", true, death.getName());

            Bukkit.getScheduler().scheduleSyncDelayedTask(registry, new BukkitRunnable() {
                @Override
                public void run() {
                    location.getBlock().setType(Material.AIR);
                    new Location(location.getWorld(), location.getX(), location.getY(), location.getZ() + 1).getBlock().setType(Material.AIR);
                    ((TNTPrimed) death.getWorld().spawnEntity(location, EntityType.PRIMED_TNT)).setFuseTicks(0); // Use this so blocks don't break

                    GlobalUtils.sendToAll("%s\'s corpse exploded!", true, death.getName());
                }
            }, 300);
        }

        if (event.getEntity().getKiller() != null && event.getEntity().getKiller() != death) {
            Player killer = event.getEntity().getKiller();

            event.setDeathMessage(String.format(ChatColor.RED + "%s was slain by %s", death.getName(), killer.getName()));

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
    public void onExplode(EntityExplodeEvent event) {
        event.blockList().clear();
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (event.getItem().equals(ItemUtils.Items.getGoldenHead())) {
            player.getActivePotionEffects().clear();
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 2));
            GlobalUtils.sendMessage(player, ChatColor.GREEN + "You now have regeneration 2 for 10 seconds!", false);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (UHCPlugin.useCutclean) {
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(ItemUtils.Inventories.getSpectatorInventory().getTitle()) && event.getCurrentItem() != null) {
            if (event.getCurrentItem().getType() == Material.SKULL_ITEM) {
                teleportTo((Player) event.getWhoClicked(), Bukkit.getPlayer(((SkullMeta) event.getCurrentItem().getItemMeta()).getOwner()));
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
            event.getEntity().setMetadata("theProjectileLauncher", new FixedMetadataValue(registry, ((Player) event.getEntity().getShooter()).getName()));
        }
    }

    @EventHandler
    public void onDamage(PlayerDropItemEvent event) {
        if (deathList.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && deathList.contains(event.getDamager().getName())) {
            event.setCancelled(true);
            return;
        }

        if (UHCPlugin.showProjectilePlayerHealth) {
            if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
                if (event.getDamager().hasMetadata("theProjectileLauncher")) {
                    Player player = Bukkit.getPlayer(event.getDamager().getMetadata("theProjectileLauncher").get(0).asString());
                    if (player.isOnline() && player.getGameMode() != GameMode.SURVIVAL) {
                        GlobalUtils.sendMessage(player, "%s is now on %s health!", false, event.getEntity().getName(), ((Player) event.getEntity()).getHealth() - event.getDamage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        if (deathList.contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        if (!UHCPlugin.usePearlDamage && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getTo());
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (deathList.contains(event.getEntity().getName())) {
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
        } else {
            if (event.getItem() != null && event.getItem().getType().equals(Material.SKULL_ITEM) && event.getItem().getData().getData() == 3) {
                if (event.getItem().getAmount() == 1) {
                    event.getItem().setType(Material.AIR);
                } else {
                    event.getItem().setAmount(event.getItem().getAmount() - 1);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (UHCPlugin.useCutclean) {
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
        if (UHCPlugin.useAppleLootRates || UHCPlugin.useFlintLootRates) {
            if (event.isDropItems()) {
                switch (event.getBlock().getType()) {
                    case LEAVES:
                    case LEAVES_2:
                        event.setDropItems(false);
                        if (new Random().nextInt(100) <= 3) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SAPLING, 1));
                        }
                        if (!UHCPlugin.useAppleFamine) {
                            if (new Random().nextInt(UHCPlugin.appleDropChance) == 0) {
                                event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.APPLE));
                            }
                        }
                        break;
                    case GRAVEL:
                        event.setDropItems(false);
                        if (new Random().nextInt(UHCPlugin.flintDropChance) == 0) {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.FLINT));
                        } else {
                            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.GRAVEL));
                        }
                        break;
                }
            }
        }
    }

    private void teleportTo(Player toTeleport, Player teleportTo) {
        toTeleport.teleport(teleportTo);
        GlobalUtils.sendMessage(toTeleport, ChatColor.RED + "Teleported to %s", false, teleportTo.getName());
    }

    private void updateScoreboard(Player player, boolean remove) {
        if (!remove) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("uhc", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b&lUHC"));

            Score score = objective.getScore(ChatColor.GREEN + "Kills:");
            score.setScore(kills.get(player));

            player.setScoreboard(scoreboard);
        } else {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
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

    public static UHCEvents instance() {
        return instance;
    }
}
