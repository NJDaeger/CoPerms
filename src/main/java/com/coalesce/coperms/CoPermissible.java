package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class CoPermissible extends PermissibleBase {
	
	private final CoUser user;
	
	public CoPermissible(Player player) {
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
		System.out.println(permission);
		if (user.hasPermission("*")) return true;
		return user.getPermissions().contains(permission);
	}
	
	@Override
	public boolean hasPermission(Permission permission) {
		if (user.hasPermission("*")) return true;
		return user.getPermissions().contains(permission.getName());
	}
	
	private boolean hasWildcardNode(Set<String> permissions, String permission) {
		if (permission.contains(".")) {
			String[] sp = permission.split("\\.");
			List<String> node = Arrays.asList(sp);
			
		}
		return false; //we now know that the permission being checked doesnt have a wildcard permission
	}
}
