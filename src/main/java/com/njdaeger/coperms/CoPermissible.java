package com.njdaeger.coperms;

import com.njdaeger.coperms.data.CoUser;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

public class CoPermissible extends PermissibleBase {

    private final CoUser user;

    CoPermissible(Player player) {
        super(player);
        this.user = CoPerms.getInstance().getUser(player.getWorld(), player.getUniqueId());
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return hasPermission(permission);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return hasPermission(permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        if (isOp()) {
            return true;
        }
        return user.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        return hasPermission(permission.getName());
    }
}
