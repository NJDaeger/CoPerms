package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CoUser {
	
	/*
	This will store the users group, current world, user ID, the user section, all the permissions the user has.
	 */
	private Group group; //From load method
	private CoWorld world; //From load method
	private final UUID uuid;
	private final CoPerms plugin;
	private ISection userSection; //From load method
	private final Set<Group> groups; //From getGroups
	private final Set<String> permissions; //From load method
	
	public CoUser(CoPerms plugin, UUID userID) {
		this.permissions = new HashSet<>();
		this.groups = new HashSet<>();
		this.plugin = plugin;
		this.uuid = userID;
	}
	
	/**
	 * Gets the current group of the user
	 * @return The users current group
	 */
	public Group getGroup() {
		return group;
	}
	
	/**
	 * Gets all the groups the user is in from every world
	 * @return All the user groups
	 */
	public Set<Group> getGroups() {
		groups.clear();
		for (CoWorld world : plugin.getDataHolder().getWorlds().values()) {
			if (world.hasUser(uuid)) {
				groups.add(plugin.getDataHolder().getGroup(world.getUserDataFile().getEntry("users." + uuid.toString() + ".group").getString()));
			}
		}
		return groups;
	}
	
	/**
	 * Gets the user UUID
	 * @return The user UUID
	 */
	public UUID getUserID() {
		return uuid;
	}
	
	/**
	 * Gets the section of the user in its current world.
	 * @return The user section
	 */
	public ISection getUserSection() {
		return userSection;
	}
	
	/**
	 * Gets all the permissions specified in the user's current world UserDataFile
	 * @return All the user permissions in this world
	 */
	public Set<String> getUserPermissions() {
		return new HashSet<>(userSection.getEntry("permissions").getStringList());
	}
	
	/**
	 * All the permissions the user has, including the ones given from the group its currently in
	 * @return All the user permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}
	
	/**
	 * Gets the current world of the user.
	 * @return The users current world
	 */
	public CoWorld getWorld() {
		return world;
	}
	
	/**
	 * Loads a user into a world
	 * @param world The world to load the user into
	 */
	public void load(CoWorld world) {
		this.world = world;
		this.userSection = world.getUserDataFile().getSection("users." + uuid.toString());
		this.group = world.getGroup(userSection.getEntry("group").getString());
		group.addUser(uuid);
		resolvePermissions();
	}
	
	/**
	 * Resolves the user permissions
	 */
	private void resolvePermissions() {
	
	}
}
