package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class DataHolder {
	
	/*
	this needs to hold all the worlds, the users, and the groups
	 */
	private final CoPerms plugin;
	private final DataLoader data;
	private final Map<UUID, CoUser> users;
	private final Map<String, Group> groups;
	private final Map<String, CoWorld> worlds;
	
	public DataHolder(DataLoader dataloader, CoPerms plugin) {
		this.plugin = plugin;
		this.data = dataloader;
		this.users = new HashMap<>();
		this.worlds = new HashMap<>();
		this.groups = new HashMap<>();
		
	}
	
	/**
	 * Gets a user from a world user file.
	 * @param world The world to get the user from.
	 * @param user The user to get.
	 * @return The user.
	 *
	 * <p>Note: The user can be offline
	 */
	public CoUser getUser(World world, UUID user) {
		return null;
	}
	
	/**
	 * Gets a user from a world user file
	 * @param world The world to get the user from
	 * @param user The user to get.
	 * @return The user.
	 *
	 * <p>Note: the user can be offline
	 */
	public CoUser getUser(String world, UUID user) {
		return null;
	}
	
	/**
	 * Gets an online user
	 * @param uuid The user to get
	 * @return The user
	 */
	public CoUser getUser(UUID uuid) {
		return users.get(uuid);
	}
	
	/**
	 * Gets a world via the Bukkit world object
	 * @param world The world to get
	 * @return The corresponding CoWorld
	 */
	public CoWorld getWorld(World world) {
		return null;
	}
	
	/**
	 * Gets a world via its name
	 * @param name The name of the world to get
	 * @return The corresponding CoWorld
	 */
	public CoWorld getWorld(String name) {
		return null;
	}
	
	/**
	 * A map of all the world names and the corresponding CoWorlds
	 * @return The current CoWorlds loaded.
	 */
	public Map<String, CoWorld> getWorlds() {
		return null;
	}
	
	/**
	 * A map of all the user UUID's and the corresponding CoUsers
	 * @return The current CoUsers loaded.
	 */
	public Map<UUID, CoUser> getUsers() {
		return null;
	}
	
	/**
	 * Gets a group from a world.
	 * @param world The world to get the group from
	 * @param name The name of the group
	 * @return The group if exists
	 */
	public Group getGroup(World world, String name) {
		return null;
	}
	
	/**
	 * Gets a group via name
	 * @param name The name of the group
	 * @return The group if exists
	 */
	public Group getGroup(String name) {
		return null;
	}
	
	/**
	 * Gets a map of all the groups
	 * @return All the groups loaded.
	 */
	public Map<String, Group> getGroups() {
		return null;
	}
	
	public CoUser loadUser(World world, UUID userID) {
		if (getUser(userID) != null) {
			getWorld(world).unloadUser(getUser(userID));
			getUser(userID).load(getWorld(world));
			return getUser(userID);
		}
		this.users.put(userID, new CoUser(plugin, userID));
		getWorld(world).loadUser(getUser(userID));
		return getUser(userID);
		/*
		check if the user exists in the user map, if the user exists then return the user
		
		if the user doesnt exist, then get the user's world and load the user into the world
		
		reload the user permissions
		 */
	}
	
	public void unloadUser(UUID userID) {
	
	}
	
}
