package com.njdaeger.coperms;

import com.njdaeger.coperms.data.CoUser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

public class CoPermissible extends PermissibleBase {

    private final CoUser user;

    CoPermissible(Player player) {
        super(player);
        this.user = CoPerms.getPlugin(CoPerms.class).getDataHolder().getUser(player.getUniqueId());
    }

    @Override
    public boolean isPermissionSet(String permission) {
        return user.getPermissions().contains(permission);
    }

    @Override
    public boolean isPermissionSet(Permission permission) {
        return user.getPermissions().contains(permission.getName());
    }

    @Override
    public boolean hasPermission(String permission) {
        if (isOp()) {
            return true;
        }
        if (user.hasPermission("*")) {
            return true;
        }
        if (hasNegatedNode(permission)) {
            return false;
        }
        if (hasWildcardNode(permission)) {
            return true;
        }
        return user.getPermissions().contains(permission);
    }

    @Override
    public boolean hasPermission(Permission permission) {
        if (isOp()) {
            return true;
        }
        if (user.hasPermission("*")) {
            return true;
        }
        if (hasNegatedNode(permission.getName())) {
            return false;
        }
        if (hasWildcardNode(permission.getName())) {
            return true;
        }
        return user.getPermissions().contains(permission.getName());
    }

    private boolean hasWildcardNode(String permission) {
        if (permission.contains(".")) {
            if (user.getWildcardNodes().contains(permission)) {
                return true;
            }
            for (int i = 0; i < StringUtils.countMatches(permission, "."); i++) {
                if (permission.endsWith(".*")) {
                    permission = permission.substring(0, permission.lastIndexOf("."));
                }
                permission = permission.substring(0, permission.lastIndexOf(".")) + ".*";
                if (user.getWildcardNodes().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasNegatedNode(String permission) {
        if (user.getNegationNodes().contains("-" + permission) && !user.getPermissions().contains(permission)) {
            if (permission.endsWith(".*") && user.getWildcardNodes().contains(permission)) {
                for (int i = 0; i < StringUtils.countMatches(permission, "."); i++) {
                    if (permission.endsWith(".*")) {
                        permission = permission.substring(0, permission.lastIndexOf("."));
                    }
                    permission = permission.substring(0, permission.lastIndexOf(".")) + ".*";
                    if (user.getNegationNodes().contains(permission)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
        return false;
    }
}
