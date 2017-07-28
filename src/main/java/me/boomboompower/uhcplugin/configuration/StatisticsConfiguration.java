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
import org.bukkit.entity.Player;

import java.io.*;

public class StatisticsConfiguration implements ConfigurationBase {

    private Player player;

    private File directory = new File("stats");

    public StatisticsConfiguration(Player playerIn) {
        this.player = playerIn;
    }

    @Override
    public void save() {
        if (player == null) return;

        try {
            createDirectory();

            File file = new File(directory, player.getUniqueId().toString());

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            JsonObject settings = new JsonObject();

            settings.addProperty("kills", PlayerListener.getInstance().getKills().get(player));

            bufferedWriter.write(settings.toString());
            bufferedWriter.close();
            writer.close();
        } catch (Throwable throwable) {
            UHCPlugin.log("Failed to save %s config file", (player.getName().endsWith("s") ? player.getName() + "'" : player.getName() + "'s"));
        }
    }

    @Override
    public void load() {
        if (player == null) return;

        try {
            File file = new File(directory, player.getUniqueId().toString());

            if (!file.exists()) {
                save();
                UHCPlugin.log("%s config does not exist, saving instead", (player.getName().endsWith("s") ? player.getName() + "'" : player.getName() + "'s"));
            }

            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder builder = new StringBuilder();

            String current;
            while ((current = bufferedReader.readLine()) != null) {
                builder.append(current);
            }
            JsonObject settings = new JsonParser().parse(builder.toString()).getAsJsonObject();

            if (settings.has("kills")) {
                PlayerListener.getInstance().getKills().put(player, settings.get("kills").getAsInt());
            }

        } catch (Throwable throwable) {
            save();
            UHCPlugin.log("Failed to load %s config file, saving", (player.getName().endsWith("s") ? player.getName() + "'" : player.getName() + "'s"));
        }
    }

    @Override
    public void createDirectory() {
        if (directory.exists()) {
            return;
        }
        if (directory.mkdirs()) {
            UHCPlugin.log("Successfully created statistics directory");
        } else {
            UHCPlugin.log("Could not create statistics directory");
        }
    }

    @Override
    public File getConfigFile() {
        return new File(directory, player.getUniqueId().toString());
    }

    public void setPlayer(Player playerIn) {
        this.player = playerIn;
    }
}
