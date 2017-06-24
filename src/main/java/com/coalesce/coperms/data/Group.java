package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.exceptions.GroupInheritMissing;
import com.coalesce.coperms.exceptions.SuperGroupMissing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Group {
	
	/*
	group permissions, users, the world the group belongs to and the name (eventually the prefixes and suffixes)
	 */
	
	private final List<String> inheritance;
	private final Set<String> permissions;
	private final boolean isDefault;
	private final ISection section;
	private final Set<UUID> users;
	private final CoPerms plugin;
	private final CoWorld world;
	private final String name;
	private final int rankID;
	
	
	public Group(CoPerms plugin, CoWorld world, String name) {
		this.section = world.getGroupDataFile().getSection("groups." + name);
		this.permissions = new HashSet<>(section.getEntry("permissions").getStringList());
		this.inheritance = section.getEntry("inherits").getStringList();
		this.rankID = section.getEntry("info.rankid").getInt();
		this.isDefault = rankID == 0;
		this.users = new HashSet<>();
		this.plugin = plugin;
		this.world = world;
		this.name = name;
	}
	
	/**
	 * Gets the name of this group
	 * @return The group name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the rank ID number. This is used to determine the rank tree
	 * @return The id of this rank
	 */
	public int getRankID() {
		return rankID;
	}
	
	/**
	 * Checks if this group is a default group or not
	 * @return If this group is default or not.
	 */
	public boolean isDefault() {
		return isDefault;
	}
	
	/**
	 * Gets the permissions of this group
	 * @return The group permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}
	
	/**
	 * Adds a user to this group
	 * @param user The user to add
	 */
	public boolean addUser(UUID user) {
		return users.add(user);
	}
	
	/**
	 * Removes a user from this group
	 * @param user The user to remove
	 */
	public boolean removeUser(UUID user) {
		return users.remove(user);
	}
	
	/**
	 * Checks if this group has a user in it
	 * @param user The user to look for
	 * @return True if the user is currently in it, false otherwise
	 */
	public boolean hasUser(UUID user) {
		return users.contains(user);
	}
	
	/**
	 * Gets a user from this group
	 * @param user The user to get
	 * @return The user if online.
	 */
	public CoUser getUser(UUID user) {
		return world.getUser(user);
	}
	
	/**
	 * Gets the world this group belongs too.
	 * @return The world of this group.
	 */
	public CoWorld getWorld() {
		return world;
	}
	
	public boolean addPermission(String permission) {
		boolean ret;
		ret = permissions.add(permission);
		section.getEntry("permissions").setValue(permissions.toArray());
		reloadUsers();
		return ret;
	}
	
	public boolean removePermission(String permission) {
		boolean ret;
		ret = permissions.remove(permission);
		section.getEntry("permissions").setValue(permissions.toArray());
		reloadUsers();
		return ret;
	}
	
	public boolean addInheritance(String group) {
		boolean ret;
		ret = inheritance.add(group);
		section.getEntry("inherits").setValue(inheritance.toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	public boolean removeInheritance(String group) {
		boolean ret;
		ret = inheritance.remove(group);
		section.getEntry("inherits").setValue(inheritance.toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	public List<String> getInheritancetree() {
		return inheritance;
	}
	
	void loadInheritanceTree() {
		inheritance.forEach(key -> {
			if (key.startsWith("s:")) {
				if (plugin.getDataHolder().getSuperGroup(key.split("s:")[1]) == null) {
					throw new SuperGroupMissing();
				}
				permissions.addAll(plugin.getDataHolder().getSuperGroup(key.split("s:")[1]).getPermissions());
			}
			if (!world.getGroups().containsKey(key)) {
				throw new GroupInheritMissing();
			}
			world.getGroup(key).loadInheritanceTree();
			permissions.addAll(world.getGroup(key).getPermissions());
			
		});
	}
	
	private void reloadUsers() {
		users.forEach(u -> getUser(u).resolvePermissions());
	}
	
}
