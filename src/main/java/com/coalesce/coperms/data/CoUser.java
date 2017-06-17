package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CoUser {
	
	private Group group;
	private CoWorld world;
	private final UUID uuid;
	private final CoPerms plugin;
	private final ISection userSection;
	private final Set<String> permissions;
	
	public CoUser(CoPerms plugin, UUID userID, ISection userSection, CoWorld world, Group group) {
		this.permissions = new HashSet<>();
		this.userSection = userSection;
		this.plugin = plugin;
		this.world = world;
		this.group = group;
		this.uuid = userID;
	}
	
	public ISection getUserSection() {
		return userSection;
	}
	
	/**
	 * Gets the permissions specified by the user entry in the user file
	 * @return The specific permissions from the user file.
	 */
	public Set<String> getUserPermissions() {
		return new HashSet<>(userSection.getEntry("permissions").getStringList());
	}
	
	/**
	 * Gets all the permissions from the user.
	 * @return All the user permissions.
	 */
	public Set<String> getPermissions() {
		return permissions;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public String getGroupName() {
		return userSection.getEntry("group").getString();
	}
	
	public void setGroup(String name, CoWorld world) {
	
	}
	
	public void setGroup() {
	
	}
	
	public CoWorld getWorld() {
		return world;
	}
	
	public CoUser load(CoWorld world) {
		this.world = world;
		Player player = Bukkit.getPlayer(uuid);
		reloadPermissions(player, world);
		return this;
	}
	
	/*
	Loading players into worlds:
	
	1. Unload the user from the previous world
		1. Unload the user CoUser#unload(CoWorld world)
			- Removes all the permissions from the user
		2. Unload the user from the world CoWorld#unloadUser(UUID user)
			- Removes the user from the map in the CoWorld
	2. Start to load the user in the new world
		1. Load the user into the world CoWorld#loadUser(UUID user)
			- puts the user into the user map
		2. Load the user permissions with CoUser#load(CoWorld)
			- Gets all the user permission and group permissions.
	 */
	
	/*
	
	On world change:
	
	1. Unload the user from the world
	
	2. Load the user to the new world
	
	 */
	
	public void unload(CoWorld world) {
	
	}
	
	/**
	 * Loads a player into another world
	 * @param world The next world for the player
	 */
	public void worldChange(CoWorld world) {
		this.world.unloadUser(uuid);
		this.world = world;
		this.world.loadUser(uuid);
	}
	
	public boolean hasPermission(String node) {
		return permissions.contains(node);
	}
	
	private void reloadPermissions(Player player, CoWorld world) {
		long time = System.currentTimeMillis();
		Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
		Set<PermissionAttachmentInfo> pBlacklist = new HashSet<>();
		Set<String> blacklist = new HashSet<>();
		perms.clear();
		permissions.clear();
		permissions.addAll(world.getGroup(uuid).getPermissions());
		permissions.addAll(getUserPermissions());
		permissions.forEach(node -> {
			if (node.startsWith("-")) {
				blacklist.add(node.substring(1));
			}
			perms.add(new PermissionAttachmentInfo(player, node, new PermissionAttachment(plugin, player),true));
		});
		permissions.removeAll(blacklist);
		blacklist.forEach(node -> perms.forEach(perm -> {
			if (perm.getAttachment().getPermissions().containsKey(node)) {
				pBlacklist.add(perm);
			}
		}));
		perms.removeAll(pBlacklist);
		System.out.println(System.currentTimeMillis() - time);
		
	}
	
	
	
}
