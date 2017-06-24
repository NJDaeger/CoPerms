package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class CoUser {
	
	/*
	This will store the users group, current world, user ID, the user section, all the permissions the user has.
	 */
	private String name; //From load method
	private Group group; //From load method
	private CoWorld world; //From load method
	private final UUID uuid;
	private final CoPerms plugin;
	private ISection userSection; //From load method
	private final Set<Group> groups; //From getGroups
	//private PermissionAttachment perms; //From load method
	private final Set<String> wildcards; //From resolve method
	private final Set<String> negations; //From resolve method
	private final Set<String> permissions; //From resolve method
	
	public CoUser(CoPerms plugin, UUID userID) {
		this.permissions = new HashSet<>();
		this.wildcards = new HashSet<>();
		this.negations = new HashSet<>();
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
	 * Gets the name of the user
	 * @return The name of the user.
	 */
	public String getName() {
		return name;
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
	 * Sets the group of a user
	 * @param world The world to set the group of the user in
	 * @param name The name of the group
	 * @return Whether the user was added or not.
	 */
	public void setGroup(CoWorld world, String name) {
		if (group != null) group.removeUser(uuid);
		userSection.getEntry("group").setValue(name);
		this.group = world.getGroup(name);
		this.group.addUser(uuid);
		resolvePermissions();
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
	 * Checks if the user has a permission or not.
	 * @param node The node to check
	 * @return True if the user has the permission, false otherwise.
	 */
	public boolean hasPermission(String node) {
		return permissions.contains(node);
	}
	
	/**
	 * Adds a permission to the users permissions.
	 * @param node The permission to add
	 * @return If the permission was added or not.
	 */
	public boolean addPermission(String node) {
		boolean ret;
		List<String > perms = getUserSection().getEntry("permissions").getStringList();
		ret = perms.add(node);
		userSection.getEntry("permissions").setValue(perms.toArray());
		resolvePermissions();
		return ret;
	}
	
	/**
	 * Removes a permission from the users permissions.
	 * @param node The permission to add
	 * @return If the permission was added or not.
	 */
	public boolean removePermission(String node) {
		boolean ret;
		List<String > perms = getUserSection().getEntry("permissions").getStringList();
		ret = perms.remove(node);
		userSection.getEntry("permissions").setValue(perms.toArray());
		resolvePermissions();
		return ret;
	}
	
	/**
	 * Loads a user into a world
	 * @param world The world to load the user into
	 */
	public void load(CoWorld world) {
		
		this.world = world; //Provided
		this.userSection = world.getUserDataFile().getSection("users." + uuid.toString()); //From world user file
		this.group = world.getGroup(userSection.getEntry("group").getString()); //From user section
		if (group == null) setGroup(world, world.getDefaultGroup().getName());
		this.name = userSection.getEntry("username").getString(); //From user section
		//if (perms == null) perms = Bukkit.getPlayer(uuid).addAttachment(plugin);
		this.group.addUser(uuid); //Group exists, we just need to add the user to it.
		resolvePermissions();
	}
	
	/**
	 * Unloads a user from any world.
	 */
	public void unload() {
		this.group.removeUser(uuid);
		this.permissions.clear();
		this.wildcards.clear();
		this.negations.clear();
		this.userSection = null;
		this.world = null;
		this.group = null;
	}
	
	/**
	 * Gets all the wildcard permissions
	 * @return The user wildcard permissions
	 */
	public Set<String> getWildcardNodes() {
		return wildcards;
	}
	
	/**
	 * Gets all the negated nodes. (including negated wildcards)
	 * @return The user negated nodes.
	 */
	public Set<String> getNegationNodes() {
		return negations;
	}
	
	/**
	 * Resolves the user permissions
	 */
	public void resolvePermissions() {
		
		permissions.clear();
		//perms.getPermissions().clear();
		group.getPermissions().forEach(System.out::println);
		this.permissions.addAll(group.getPermissions());
		this.permissions.addAll(getUserPermissions());
		
		permissions.forEach(node -> {
			if (node.endsWith(".*")) {
				wildcards.add(node);
			}
			if (node.startsWith("-")) {
				negations.add(node);
			}
		});
	}
}
