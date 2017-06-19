package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class Group {
	
	/*
	group permissions, users, the world the group belongs to and the name (eventually the prefixes and suffixes)
	 */
	
	private final Set<String> permissions;
	private final boolean isDefault;
	private final ISection section;
	private final Set<UUID> users;
	private final CoPerms plugin;
	private final CoWorld world;
	private final String name;
	
	
	public Group(CoPerms plugin, CoWorld world, String name) {
		this.section = world.getGroupDataFile().getSection("groups." + name);
		this.permissions = new HashSet<>(section.getEntry("permissions").getStringList());
		this.isDefault = section.getEntry("default").getBoolean();
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
	 * Checks if this group is a default group or not
	 * @return
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
	public void addUser(UUID user) {
		users.add(user);
	}
	
	/**
	 * Removes a user from this group
	 * @param user The user to remove
	 */
	public void removeUser(UUID user) {
		users.remove(user);
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
	
}
