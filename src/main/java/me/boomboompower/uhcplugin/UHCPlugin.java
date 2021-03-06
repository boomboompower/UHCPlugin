package me.boomboompower.uhcplugin;

import me.boomboompower.uhcplugin.commands.CommandBase;
import me.boomboompower.uhcplugin.commands.GameCommand;
import me.boomboompower.uhcplugin.commands.PermissionsCommand;
import me.boomboompower.uhcplugin.commands.UHCCommand;
import me.boomboompower.uhcplugin.configuration.MainConfiguration;
import me.boomboompower.uhcplugin.configuration.StatisticsConfiguration;
import me.boomboompower.uhcplugin.listeners.*;
import me.boomboompower.uhcplugin.items.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class UHCPlugin extends JavaPlugin {

    public static String PREFIX = ChatColor.GOLD + "UHC" + ChatColor.AQUA + " > " + ChatColor.GRAY;

    public State currentGamestate = State.WAITING;

    public int flintDropChance = 9;
    public int appleDropChance = 100;
    public int timeBombDelay = 30;

    public boolean showProjectilePlayerHealth = false;
    public boolean useFlintLootRates = false;
    public boolean useAppleLootRates = false;
    public boolean backbacksEnabled = false;
    public boolean useAppleFamine = false;
    public boolean usePearlDamage = true;
    public boolean useTimeBomb = false;
    public boolean useCutclean = false;
    public boolean dropHeads = true;

    private static UHCPlugin instance;

    private static StatisticsConfiguration statisticsConfiguration;
    private static MainConfiguration mainConfiguration;

    @Override
    public void onEnable() {
        instance = this;

        register(new UHCListener(), new WorldListener(), new PlayerListener(), new SpectatorListener(), new EntityListener());
        registerCommands(new UHCCommand(), new GameCommand(), new PermissionsCommand());

        mainConfiguration = new MainConfiguration(new File("uhc"), "main.txt");
        mainConfiguration.load();

        Bukkit.addRecipe(getGoldenHeadRecipe());
    }

    @Override
    public void onDisable() {
        mainConfiguration.save();
    }

    private void register(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    private void registerCommands(CommandBase... executors) {
        for (CommandBase executor : executors) {
            Bukkit.getPluginCommand(executor.getCommand()).setExecutor(executor);
        }
    }

    private Recipe getGoldenHeadRecipe() {
        ShapedRecipe recipe = new ShapedRecipe(ItemUtils.Items.getGoldenHead());
        recipe.shape("GGG", "GSG", "GGG");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('S', new MaterialData(Material.SKULL_ITEM, (byte) 3));
        return recipe;
    }

    public static boolean hasStarted() {
        return getInstance().currentGamestate == State.STARTED;
    }

    public enum State { WAITING, STARTING, STARTED, FINISHED }

    public static void log(String message, Object... formatting) {
        getInstance().getLogger().info(String.format(message, formatting));
    }

    public static MainConfiguration getMainConfig() {
        return mainConfiguration;
    }

    public static UHCPlugin getInstance() {
        return instance;
    }
}
