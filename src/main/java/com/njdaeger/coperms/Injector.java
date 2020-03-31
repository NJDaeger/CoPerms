package com.njdaeger.coperms;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class Injector {

    /**
     * Injects a custom permissible into the user object
     *
     * @param player The player to inject the new permissible into
     */
    public static void inject(Player player) {

        Field field;
        String v = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";

        try {
            Class<?> humanEntity = Class.forName("org.bukkit.craftbukkit." + v + "entity.CraftHumanEntity");

            field = humanEntity.getDeclaredField("perm");
            field.setAccessible(true);
            field.set(player, new CoPermissible(player));
            Bukkit.getScheduler().runTask(CoPerms.getInstance(), player::updateCommands);

        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
