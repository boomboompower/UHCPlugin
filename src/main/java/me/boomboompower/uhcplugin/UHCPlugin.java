package me.boomboompower.uhcplugin;

import me.boomboompower.uhcplugin.commands.CommandBase;
import me.boomboompower.uhcplugin.commands.UHCCommand;

import me.boomboompower.uhcplugin.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

public final class UHCPlugin extends JavaPlugin {

    public static String PREFIX = ChatColor.GOLD + "UHC" + ChatColor.AQUA + " > " + ChatColor.GRAY;

    public static int flintDropChance = 9;
    public static int appleDropChance = 100;
    public static int timeBombDelay = 30;

    public static boolean showProjectilePlayerHealth = false;
    public static boolean useFlintLootRates = false;
    public static boolean useAppleLootRates = false;
    public static boolean backbacksEnabled = false;
    public static boolean useAppleFamine = false;
    public static boolean usePearlDamage = true;
    public static boolean useTimeBomb = false;
    public static boolean useCutclean = false;
    public static boolean dropHeads = true;

    @Override
    public void onEnable() {
        register(new UHCEvents(this));
        registerCommands(new UHCCommand());

        Bukkit.addRecipe(getGoldenHeadRecipe());
    }

    @Override
    public void onDisable() {
    }

    private void register(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommands(CommandBase executor) {
        Bukkit.getPluginCommand(executor.getCommand()).setExecutor(executor);
    }

    private Recipe getGoldenHeadRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(ItemUtils.Items.getGoldenHead());
        recipe.shape("GGG", "GSG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('S', new MaterialData(Material.SKULL_ITEM, (byte) 3));
        return recipe;
    }
}
