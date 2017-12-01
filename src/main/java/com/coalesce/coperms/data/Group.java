package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.DataLoader;
import com.coalesce.coperms.exceptions.GroupInheritMissing;
import com.coalesce.coperms.exceptions.SuperGroupMissing;
import com.coalesce.core.config.base.ISection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Group {
	
	private final Set<String> groupPermissions;
	private final List<String> inheritance;
	private final Set<String> permissions;
	private final DataLoader loader;
	private final boolean isDefault;
	private final ISection section;
	private final Set<UUID> users;
	private final CoPerms plugin;
	private final CoWorld world;
	private final String name;
	private final int rankID;
	private boolean canBuild;
	private String prefix;
	private String suffix;
	
	
	public Group(CoPerms plugin, CoWorld world, String name, DataLoader loader) {
		this.section = world.getGroupDataFile().getSection("groups." + name);
		this.groupPermissions = new HashSet<>(section.getEntry("permissions").getStringList());
		this.rankID = section.getEntry("info.rankid").getAs(Integer.class);
		this.inheritance = section.getEntry("inherits").getStringList();
		this.canBuild = section.getEntry("info.canBuild").getBoolean();
		this.prefix = section.getEntry("info.prefix").getString();
		this.suffix = section.getEntry("info.suffix").getString();
		this.permissions = new HashSet<>();
		this.isDefault = rankID == 0;
		this.users = new HashSet<>();
		this.loader = loader;
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
	 * Gets the group prefix
	 * @return The group prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Sets the group prefix
	 * @param prefix The new group prefix
	 */
	public void setPrefix(String prefix) {
		addInfo("prefix", prefix);
		this.prefix = prefix;
	}
	
	/**
	 * Gets the group suffix
	 * @return The group suffix
	 */
	public String getSuffix() {
		return suffix;
	}
	
	/**
	 * Sets the group suffix
	 * @param suffix The new suffix
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
		section.getConfig().setEntry(section.getCurrentPath() + ".info." + node, value);
	}
	
	/**
	 * Gets a node from the user section
	 * @param node The node path to get
	 * @return The value of the node, null if it doesn't exist.
	 */
	public Object getInfo(String node) {
		if (section.getSection("info").getEntry(node) == null) return null;
		return section.getSection("info").getEntry(node).getValue();
	}
	
	/**
	 * Checks if the group has permissions to build
	 * @return True if the group can build
	 */
	public boolean canBuild() {
		return canBuild;
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
	 * Gets all the permissions of this group. (including inherited permissions)
	 * @return The group permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}
	
	/**
	 * Gets the permissions that are specified for this group
	 * @return THe group private permissions.
	 */
	public Set<String> getGroupPermissions() {
		return groupPermissions;
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
	
	/**
	 * Checks whether this group has a permission or not
	 * @param permission The permission to look for
	 * @return True if the group has the permission, false otherwise
	 */
	public boolean hasPermission(String permission) {
		return permissions.contains(permission);
	}
	
	/**
	 * Adds a permission to the group
	 * @param permission The permission to add
	 * @return True if it was successfully added.
	 */
	public boolean addPermission(String permission) {
		boolean ret;
		ret = getGroupPermissions().add(permission);
		section.getEntry("permissions").setValue(getGroupPermissions().toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	/**
	 * Removes a permission from the group
	 * @param permission The permission to remove
	 * @return True if the permission was successfully removed.
	 */
	public boolean removePermission(String permission) {
		boolean ret;
		ret = getGroupPermissions().remove(permission);
		section.getEntry("permissions").setValue(getGroupPermissions().toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	/**
	 * Adds an inherited group to a group
	 * @param group The group to add to the inheritance tree
	 * @return True if successfully added
	 */
	public boolean addInheritance(String group) {
		boolean ret;
		ret = inheritance.add(group);
		section.getEntry("inherits").setValue(inheritance.toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	/**
	 * Removes an inherited group from a group
	 * @param group The group to remove from the inheritance tree
	 * @return True if successfully removed
	 */
	public boolean removeInheritance(String group) {
		boolean ret;
		ret = inheritance.remove(group);
		section.getEntry("inherits").setValue(inheritance.toArray());
		loadInheritanceTree();
		reloadUsers();
		return ret;
	}
	
	/**
	 * Gets the inheritance list of the group
	 * @return The list of inherited groups
	 */
	public List<String> getInheritancetree() {
		return inheritance;
	}
	
	void loadInheritanceTree() {
		permissions.clear();
		permissions.addAll(getGroupPermissions());
		inheritance.forEach(key -> {
			if (key.startsWith("s:")) {
				if (loader.getSuperGroup(key.split("s:")[1]) == null) {
					throw new SuperGroupMissing();
				}
				else {
					permissions.addAll(loader.getSuperGroup(key.split("s:")[1]).getPermissions());
				}
			}
			else {
				if (world.getGroup(key) == null) {
					throw new GroupInheritMissing(key);
				}
				world.getGroup(key).loadInheritanceTree();
				permissions.addAll(world.getGroup(key).getPermissions());
			}
			
		});
	}
	
	private void reloadUsers() {
		users.forEach(u -> getUser(u).resolvePermissions());
	}
	
}
