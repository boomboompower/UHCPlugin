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

import org.bukkit.entity.Player;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Permissions {

    private static Player player;
    private static final File directory = new File("perms");

    private static boolean usingFile = true;

    // Stop instance creation
    private Permissions() {
    }

    public static void setPlayer(Player playerIn) {
        player = playerIn;
    }

    public static boolean isUsingFile() {
        return usingFile;
    }

    public static void setUsingFile(boolean boolIn) {
        usingFile = boolIn;
    }

    public static List<String> getPermissionsFromFile() {
        if (!usingFile) {
            return new ArrayList<>();
        }
        try {
            createDefaultDirectory();

            List<String> perms = new ArrayList<>();

            File nameFile = new File(directory, player.getName());
            File uuidFile = new File(directory, player.getUniqueId().toString());

            // Get permissions from the file with their name
            if (nameFile.exists()) {
                FileReader fileReader = new FileReader(nameFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                for (String s : bufferedReader.lines().collect(Collectors.toList())) {
                    if (s == null || removeSpecialCharacters(s).isEmpty()) {
                        continue;
                    }
                    perms.add(s);
                }
            }

            // Always override name file permissions with uuid
            if (uuidFile.exists()) {
                perms = new ArrayList<>();

                FileReader fileReader = new FileReader(uuidFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                for (String s : bufferedReader.lines().collect(Collectors.toList())) {
                    if (s == null || removeSpecialCharacters(s).isEmpty()) {
                        continue;
                    }
                    perms.add(s);
                }
            }
            return perms;
        } catch (Throwable throwable) {
            UHCPlugin.log("Issue getting permissions for %s", player.getName());
        }
        return new ArrayList<>();
    }

    public static void createDefaultDirectory() {
        if (!usingFile || directory.exists()) {
            return;
        }
        if (directory.mkdirs()) {
            UHCPlugin.log("Successfully created permissions directory!");
        } else {
            UHCPlugin.log("Could not create permissions directory");
        }
    }

    @SuppressWarnings("SimplifiableIfStatement") // Don't wanna break things
    public static boolean hasPermission(String permission) {
        if (permission == null || removeSpecialCharacters(permission).isEmpty()) {
            return false;
        }

        permission = removeSpecialCharacters(permission);

        if (player.hasPermission(permission)) {
            return true;
        }

        if ((usingFile && (Files.exists(Paths.get(new File(directory, player.getName()).getPath()))) || Files.exists(Paths.get(new File(directory, player.getUniqueId().toString()).getPath())))) {
            return getPermissionsFromFile().contains(permission);
        }
        return false;
    }

    public static void addPermission(String permission) {
        if (!usingFile || permission == null || removeSpecialCharacters(permission).isEmpty()) {
            return;
        }

        permission = removeSpecialCharacters(permission);

        try {
            createDefaultDirectory();

            File file = new File(directory, player.getUniqueId().toString());

            if (!file.exists()) {
                file.createNewFile();
            }

            ArrayList<String> perms = new ArrayList<>();
            FileWriter writer = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            perms.addAll(getPermissionsFromFile());
            perms.add(permission);

            for (String s : perms) {
                bufferedWriter.write(s + System.lineSeparator());
            }
            UHCPlugin.log("Added permission \"%s\" to %s", permission, player.getName());
        } catch (Throwable throwable) {
            UHCPlugin.log("Failed to add permission \"%s\" to player %s", permission, player.getName());
        }
    }

    private static String removeSpecialCharacters(String input) {
        StringBuilder builder = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char c : chars) {
            if (Character.isLetterOrDigit(c) || c == '.') {
                builder.append(c);
            }
        }
        return builder.toString().trim();
    }
}
