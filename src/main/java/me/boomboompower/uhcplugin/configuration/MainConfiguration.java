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

package me.boomboompower.uhcplugin.configuration;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.boomboompower.uhcplugin.UHCPlugin;
import me.boomboompower.uhcplugin.listeners.PlayerListener;

import java.io.*;

public class MainConfiguration implements ConfigurationBase {

    private File directory;
    private File file;

    public MainConfiguration(File directory, String fileName) {
        this.directory = directory;
        this.file = new File(directory, fileName);
    }

    @Override
    public void save() {
        try {
            createDirectory();

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            JsonObject settings = new JsonObject();

            // General number settings
            settings.addProperty("flintdropchance", UHCPlugin.getInstance().flintDropChance);
            settings.addProperty("appledropchance", UHCPlugin.getInstance().appleDropChance);
            settings.addProperty("timebombdelay", UHCPlugin.getInstance().timeBombDelay);

            // Player counts
            settings.addProperty("minplayercount", PlayerListener.getInstance().minPlayerCount);
            settings.addProperty("maxplayercount", PlayerListener.getInstance().maxPlayerCount);

            // Gamemode toggles etc
            settings.addProperty("showprojectilehealth", UHCPlugin.getInstance().showProjectilePlayerHealth);
            settings.addProperty("useflintlootrates", UHCPlugin.getInstance().useFlintLootRates);
            settings.addProperty("useapplelootrates", UHCPlugin.getInstance().useAppleLootRates);
            settings.addProperty("usebackpacks", UHCPlugin.getInstance().backbacksEnabled);
            settings.addProperty("useapplefamine", UHCPlugin.getInstance().useAppleFamine);
            settings.addProperty("usepearldamage", UHCPlugin.getInstance().usePearlDamage);
            settings.addProperty("usetimebomb", UHCPlugin.getInstance().useTimeBomb);
            settings.addProperty("usecutclean", UHCPlugin.getInstance().useCutclean);
            settings.addProperty("dropheads", UHCPlugin.getInstance().dropHeads);

            bufferedWriter.write(settings.toString());
            bufferedWriter.close();
            writer.close();
        } catch (Throwable throwable) {
            UHCPlugin.log("Failed to save main configuration file");
        }
    }

    @Override
    public void load() {
        try {
            if (!file.exists()) {
                save();
                UHCPlugin.log("Configuration file does not exist, saving instead");
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder builder = new StringBuilder();

            String current;
            while ((current = bufferedReader.readLine()) != null) {
                builder.append(current);
            }
            JsonObject settings = new JsonParser().parse(builder.toString()).getAsJsonObject();

            UHCPlugin.getInstance().flintDropChance = settings.has("flintdropchance") ? settings.get("flintdropchance").getAsInt() : 9;
            UHCPlugin.getInstance().appleDropChance = settings.has("appledropchance") ? settings.get("appledropchance").getAsInt() : 100;
            UHCPlugin.getInstance().timeBombDelay = settings.has("timebombdelay") ? settings.get("timebombdelay").getAsInt() : 30;

            PlayerListener.getInstance().minPlayerCount = settings.has("minplayercount") ? settings.get("minplayercount").getAsInt() : 12;
            PlayerListener.getInstance().maxPlayerCount = settings.has("maxplayercount") ? settings.get("maxplayercount").getAsInt() : 24;

            UHCPlugin.getInstance().showProjectilePlayerHealth = settings.has("showprojectilehealth") && settings.get("showprojectilehealth").getAsBoolean();
            UHCPlugin.getInstance().useFlintLootRates = settings.has("useflintlootrates") && settings.get("useflintlootrates").getAsBoolean();
            UHCPlugin.getInstance().useAppleLootRates = settings.has("useapplelootrates") && settings.get("useapplelootrates").getAsBoolean();
            UHCPlugin.getInstance().backbacksEnabled = settings.has("usebackpacks") && settings.get("usebackpacks").getAsBoolean();
            UHCPlugin.getInstance().useAppleFamine = settings.has("useapplefamine") && settings.get("useapplefamine").getAsBoolean();
            UHCPlugin.getInstance().usePearlDamage = settings.has("usepearldamage") && settings.get("usepearldamage").getAsBoolean();
            UHCPlugin.getInstance().useTimeBomb = settings.has("usetimebomb") && settings.get("usetimebomb").getAsBoolean();
            UHCPlugin.getInstance().useCutclean = settings.has("usecutclean") && settings.get("usecutclean").getAsBoolean();
            UHCPlugin.getInstance().dropHeads = settings.has("dropheads") && settings.get("dropheads").getAsBoolean();

        } catch (Throwable throwable) {
            save();
            UHCPlugin.log("Failed to load main configuration file, saving");
        }
    }

    @Override
    public void createDirectory() {
        if (directory.exists()) {
            return;
        }
        if (directory.mkdirs()) {
            UHCPlugin.log("Successfully created main configuration directory");
        } else {
            UHCPlugin.log("Could not create main configuration directory");
        }
    }

    @Override
    public File getConfigFile() {
        return this.file;
    }
}
