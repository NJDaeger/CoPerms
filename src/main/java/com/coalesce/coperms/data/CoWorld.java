package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CoWorld {
	
	/*
	This will store the world, the groups in this world, the user file, the group file, a map of users
	 */
	
	private final World world; //Set
	private final CoPerms plugin; //Set
	private final UserDataFile userData; //Set
	private final GroupDataFile groupData; //Set
	private final Map<UUID, CoUser> users; //Set
	private final Map<String, Group> groups; //Set
	
	public CoWorld(CoPerms plugin, World world, UserDataFile userData, GroupDataFile groupData) {
		this.world = world;
		this.plugin = plugin;
		this.userData = userData;
		this.groupData = groupData;
		this.users = new HashMap<>();
		this.groups = new HashMap<>();
		
		
		groupData.getSection("groups").getKeys(false).forEach(key -> groups.put(key, new Group(plugin, this, key)));
		groups.values().forEach(Group::loadInheritanceTree);
	}
	
	/**
	 * Gets the world represented by this CoWorld
	 * @return The world
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Gets the user data file this world uses
	 * @return The worlds user data file.
	 */
	public UserDataFile getUserDataFile() {
		return userData;
	}
	
	/**
	 * Gets the group data file this world uses
	 * @return The worlds group data file
	 */
	public GroupDataFile getGroupDataFile() {
		return groupData;
	}
	
	/**
	 * Gets a user from this world
	 * @param uuid The user to find
	 * @return The user if in this world, null otherwise.
	 */
	public CoUser getUser(UUID uuid) {
		return users.get(uuid);
	}
	
	/**
	 * Gets a user via their name
	 * @param name The name of the user
	 * @return The user if online
	 */
	public CoUser getUser(String name) {
		return users.get(Bukkit.getPlayer(name).getUniqueId());
	}
	
	/**
	 * Checks if the user data file of this world has a user in it.
	 * @param uuid The user to look for
	 * @return True if the user has been in this world, false otherwise.
	 */
	public boolean hasUser(UUID uuid) {
		return getUser(uuid) != null || userData.getSection("users").contains(uuid.toString());
	}
	
	/**
	 * Gets a map of all the users in this world
	 * @return The worlds user map
	 */
	public Map<UUID, CoUser> getUsers() {
		return users;
	}
	
	/**
	 * Gets a group from this world
	 * @param name The name of the group to find
	 * @return The group
	 */
	public Group getGroup(String name) {
		return groups.get(name);
	}
	
	/**
	 * Gets a map of all the groups in this world
	 * @return The group map
	 */
	public Map<String, Group> getGroups() {
		return groups;
	}
	
	/**
	 * Gets the default group of this world
	 * @return The worlds default group
	 */
	public Group getDefaultGroup() {
		for (Group group : groups.values()) {
			if (group.isDefault()) {
				return group;
			}
		}
		return null;
	}
	
	/**
	 * Loads a user to this world.
	 * @param user The user to load.
	 *
	 *          <p>FOR INTERNAL USE ONLY</p>
	 */
	public void loadUser(CoUser user) {
		this.userData.loadUser(user.getUserID());
		this.users.put(user.getUserID(), user);
		user.load(this);
		//Reload the user permissions here- does this in the load method in CoUser#load
		//Set all the correct values
	}
	
	/**
	 * Unloads a user from this world.
	 * @param user The user to unload
	 *
	 *          <p>FOR INTERNAL USE ONLY</p>
	 */
	public void unloadUser(CoUser user) {
		if (!users.containsKey(user.getUserID())) return;
		user.unload(this);
		this.users.remove(user.getUserID());
	}
	
}
