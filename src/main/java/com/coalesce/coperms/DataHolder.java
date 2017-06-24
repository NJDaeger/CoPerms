package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.coperms.data.SuperGroup;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DataHolder {
	
	private final CoPerms plugin;
	private final Map<UUID, CoUser> users;
	private final Map<String, Group> groups;
	private final Map<String, CoWorld> worlds;
	private final Map<String, SuperGroup> supers;
	
	public DataHolder(DataLoader dataloader, CoPerms plugin) {
		this.plugin = plugin;
		this.users = new HashMap<>();
		this.supers = new HashMap<>();
		this.worlds = dataloader.getWorlds();
		this.groups = new HashMap<>();
		
		worlds.forEach((name, world) -> world.getGroups().forEach(groups::putIfAbsent));
		plugin.getSuperDataFile().getSuperGroups().forEach(g -> supers.put(g.getName(), g));
		
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
		return getWorld(world).getUser(user);
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
		return getWorld(world).getUser(user);
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
	 * Gets a user by name
	 * @param name The name of the user
	 * @return the user if online
	 */
	public CoUser getUser(String name) {
		if (Bukkit.getPlayer(name) == null) return null;
		return users.get(Bukkit.getPlayer(name).getUniqueId());
	}
	
	/**
	 * Gets a world via the Bukkit world object
	 * @param world The world to get
	 * @return The corresponding CoWorld
	 */
	public CoWorld getWorld(World world) {
		return worlds.get(world.getName());
	}
	
	/**
	 * Gets a world via its name
	 * @param name The name of the world to get
	 * @return The corresponding CoWorld
	 */
	public CoWorld getWorld(String name) {
		return worlds.get(name);
	}
	
	/**
	 * A map of all the world names and the corresponding CoWorlds
	 * @return The current CoWorlds loaded.
	 */
	public Map<String, CoWorld> getWorlds() {
		return worlds;
	}
	
	/**
	 * A map of all the user UUID's and the corresponding CoUsers
	 * @return The current CoUsers loaded.
	 */
	public Map<UUID, CoUser> getUsers() {
		return users;
	}
	
	/**
	 * Gets a group from a world.
	 * @param world The world to get the group from
	 * @param name The name of the group
	 * @return The group if exists
	 */
	public Group getGroup(World world, String name) {
		return getWorld(world).getGroup(name);
	}
	
	/**
	 * Gets a group via name
	 * @param name The name of the group
	 * @return The group if exists
	 */
	public Group getGroup(String name) {
		if (!groups.containsKey(name)) return null;
		return groups.get(name);
	}
	
	/**
	 * Gets a map of all the groups
	 * @return All the groups loaded.
	 */
	public Map<String, Group> getGroups() {
		return groups;
	}
	
	/**
	 * Gets a SuperGroup if exists
	 * @param name The name of the SuperGroup
	 * @return The SuperGroup
	 */
	public SuperGroup getSuperGroup(String name) {
		if (!supers.containsKey(name)) return null;
		return supers.get(name);
	}
	
	/**
	 * Gets a list of all the SuperGroups
	 * @return The server SuperGroups
	 */
	public Map<String, SuperGroup> getSuperGroups() {
		return supers;
	}
	
	/**
	 * Loads a user into a world
	 * @param world The world to load the user into
	 * @param userID The user to load
	 * @return The loaded user
	 */
	public CoUser loadUser(World world, UUID userID) {
		if (getUser(userID) != null) {
			getWorld(world).unloadUser(getUser(userID));
			getUser(userID).load(getWorld(world));
			return getUser(userID);
		}
		this.users.put(userID, new CoUser(plugin, userID));
		getWorld(world).loadUser(getUser(userID));
		return getUser(userID);
	}
	
	/**
	 * Unloads a user from the server
	 * @param userID The user to unload
	 */
	public void unloadUser(UUID userID) {
		getUser(userID).getWorld().unloadUser(getUser(userID));
		this.users.remove(userID);
	}
	
}
