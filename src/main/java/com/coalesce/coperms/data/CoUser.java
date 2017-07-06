package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.*;

public final class CoUser {

	private String name;
	private Group group;
	private String prefix;
	private String suffix;
	private CoWorld world;
	private final UUID uuid;
	private final CoPerms plugin;
	private ISection userSection;
	private final boolean isOnline;
	private final Set<Group> groups;
	private final Set<String> wildcards;
	private final Set<String> negations;
	private final Set<String> permissions;
	private final Set<String> userPermissions;
	
	public CoUser(CoPerms plugin, UUID userID, boolean isOnline) {
		this.userPermissions = new HashSet<>();
		this.permissions = new HashSet<>();
		this.wildcards = new HashSet<>();
		this.negations = new HashSet<>();
		this.groups = new HashSet<>();
		this.isOnline = isOnline;
		this.plugin = plugin;
		this.uuid = userID;
	}
	
	/**
	 * Checks if this CoUser is an online user.
	 * @return True if online, false otherwise.
	 */
	public boolean isOnline() {
		return isOnline;
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
	 * Gets the user prefix
	 * @return The user prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Sets the user prefix
	 * @param prefix The user prefix
	 */
	public void setPrefix(String prefix) {
		addInfo("prefix", prefix);
		this.prefix = prefix;
	}
	
	/**
	 * Gets the user suffix
	 * @return The user suffix
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Sets the user suffix
	 * @param suffix The user suffix
	 */
	public void setSuffix(String suffix) {
		addInfo("suffix", suffix);
		this.suffix = suffix;
	}
	
	/**
	 * Adds info to the user section
	 * @param node The node to add
	 * @param value The value to set the node
	 */
	public void addInfo(String node, Object value) {
		userSection.getConfig().setEntry(userSection.getCurrentPath() + ".info." + node, value);
	}
	
	/**
	 * Gets a node from the user section
	 * @param node The node path to get
	 * @return The value of the node, null if it doesn't exist.
	 */
	public Object getInfo(String node) {
		if (userSection.getSection("info").getEntry(node) == null) return null;
		return userSection.getSection("info").getEntry(node).getValue();
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
	public boolean setGroup(CoWorld world, String name) {
		boolean ret = true;
		if (group != null) {
			ret = !group.getName().equalsIgnoreCase(name);
			group.removeUser(uuid);
		}
		userSection.getEntry("group").setValue(name);
		this.group = world.getGroup(name);
		this.group.addUser(uuid);
		resolvePermissions();
		return ret;
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
		return userPermissions;
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
		Set<String> perms = getUserPermissions();
		ret = perms.add(node);
		userSection.getEntry("permissions").setValue(perms.toArray());
		resolvePermissions();
		return ret;
	}
	
	/**
	 * Removes a permission from the users permissions.
	 * @param node The permission to add
	 * @return If the permission was removed or not.
	 */
	public boolean removePermission(String node) {
		boolean ret;
		Set<String> perms = getUserPermissions();
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
		this.world = world;
		this.userSection = world.getUserDataFile().getSection("users." + uuid.toString());
		this.userPermissions.addAll(userSection.getEntry("permissions").getStringList());
		this.group = world.getGroup(userSection.getEntry("group").getString());
		if (group == null) setGroup(world, world.getDefaultGroup().getName());
		this.name = userSection.getEntry("username").getString();
		this.group.addUser(uuid);
		this.prefix = (String)getInfo("prefix");
		this.suffix = (String)getInfo("suffix");
		resolvePermissions();
	}
	
	/**
	 * Unloads a user from any world.
	 */
	public void unload() {
		this.userPermissions.clear();
		this.group.removeUser(uuid);
		this.permissions.clear();
		this.wildcards.clear();
		this.negations.clear();
		this.userSection = null;
		this.suffix = null;
		this.prefix = null;
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
