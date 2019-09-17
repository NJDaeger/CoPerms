package com.njdaeger.coperms.vault;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.perm.PermissionsHandler;
import com.earth2me.essentials.perm.impl.GenericVaultHandler;
import com.njdaeger.coperms.CoPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public final class EssentialsPermissionHandler extends GenericVaultHandler {

    public static void injectHandler() {
        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            PermissionsHandler handler = Essentials.getPlugin(Essentials.class).getPermissionsHandler();
            try {
                Field field = handler.getClass().getDeclaredField("handler");
                field.setAccessible(true);
                field.set(handler, new EssentialsPermissionHandler());
                Bukkit.getLogger().info("Successfully overrode Essentials' permission handler.");
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Bukkit.getLogger().warning("Something went wrong overriding Essentials' permission handler.");
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canBuild(Player base, String group) {
        return CoPerms.getInstance().getGroup(base.getWorld(), group).canBuild();
    }
}
