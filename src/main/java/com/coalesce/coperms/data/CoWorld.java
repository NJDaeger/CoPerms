package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public final class CoWorld {
	
	private final World world;
	private final UserDataFile userData;
	private final GroupDataFile groupData;
	private final CoPerms plugin;
	private final Set<Group> groups;
	private final Map<UUID, CoUser> users;
	
	public CoWorld(CoPerms plugin, World world, UserDataFile userdata, GroupDataFile groupdata) {
		this.world = world;
		this.userData = userdata;
		this.groupData = groupdata;
		this.plugin = plugin;
		this.groups = new HashSet<>();
		this.users = new HashMap<>();
		
		groupdata.getSection("groups").getKeys(false).forEach(key -> groups.add(new Group(key, groupdata, userdata, plugin)));
		System.out.println(groupdata.getFile().getPath());
		
		if (!world.getPlayers().isEmpty()) {
			for (Player player : world.getPlayers()) {
			
			}
		}
	}
	
	/**
	 * Gets the world represented by this CoWorld
	 * @return The Bukkit world.
	 */
	public World getWorld() {
		return world;
	}
	
	/**
	 * Gets the user file for this world.
	 * @return The world user file.
	 */
	public UserDataFile getUserFile() {
		return userData;
	}
	
	/**
	 * Gets the groups file for this world.
	 * @return The world groups file.
	 */
	public GroupDataFile getGroupFile() {
		return groupData;
	}
	
	/**
	 * Gets the groups that belong to this world.
	 * @return This world's groups.
	 */
	public Set<Group> getGroups() {
		return groups;
	}
	
	/**
	 * Gets a map of all the users in this world
	 * @return The users in ths world
	 */
	public Map<UUID, CoUser> getUsers() {
		return users;
	}
	
	/**
	 * Gets the default group for this world.
	 * @return The default group in this world
	 */
	public Group getDefaultGroup() {
		for (Group group : groups) {
			if (group.isDefaultGroup()) return group;
		}
		return null;
	}
	
	/**
	 * Loads a user into the world.
	 * @param uuid The user to load.
	 * @return The user loaded.
	 */
	public CoUser loadUser(UUID uuid) {
		users.put(uuid, userData.loadUser(uuid));
		return users.get(uuid);
	}
	
	/**
	 * Unloads a user from this world.
	 * @param uuid The user to unload.
	 */
	public void unloadUser(UUID uuid) {
		users.remove(uuid);
		userData.unloadUser(uuid);
	}
}
