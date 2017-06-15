package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class DataHolder {
	
	private final DataLoader data;
	private final Set<Group> groups;
	private final Map<UUID, CoUser> users;
	private final Map<String, CoWorld> worlds;
	
	public DataHolder(DataLoader dataloader) {
		this.data = dataloader;
		this.users = new HashMap<>();
		this.worlds = new HashMap<>();
		this.groups = new HashSet<>();
		
		worlds.putAll(dataloader.getWorlds());
		worlds.forEach((s, coWorld) -> System.out.println(coWorld.getWorld().getName()));
		worlds.forEach((name, world) -> groups.addAll(world.getGroups()));
		groups.forEach(group -> System.out.println(group.getName()));
	}
	
	public CoWorld getWorld(World world) {
		return worlds.get(world.getName());
	}
	
	public Map<String, CoWorld> getWorlds() {
		return worlds;
	}
	
	public CoUser getUser(UUID uuid) {
		if (Bukkit.getPlayer(uuid) != null) {
			for (UUID id : users.keySet()) {
				if (id.equals(uuid)) {
					return users.get(id);
				}
			}
		}
		return users.get(uuid);
	}
	
	/**
	 * Gets all the users on the server.
	 * @return All the users
	 */
	public Map<UUID, CoUser> getUsers() {
		return users;
	}
	
	/**
	 * Gets all the groups on the server.
	 * @return All the groups on this server
	 */
	public Set<Group> getGroups() {
		return groups;
	}
	
	/**
	 * Gets a group by name
	 * @param name The name of the group
	 * @return The group if found.
	 */
	public Group getGroup(String name) {
		for (Group group : groups) {
			if (group.getName().equalsIgnoreCase(name)) {
				return group;
			}
		}
		return null;
	}
	
	/**
	 * Loads a user into the server.
	 * @param uuid The user to load.
	 * @return The user loaded.
	 */
	public CoUser loadUser(UUID uuid) {
		if (getUser(uuid) != null) return getUser(uuid);
		users.put(uuid, getWorld(Bukkit.getPlayer(uuid).getWorld()).loadUser(uuid));
		return users.get(uuid);
	}
	
	/**
	 * Unloads the user from the server.
	 * @param uuid The user to unload
	 */
	public void unloadUser(UUID uuid) {
		users.remove(uuid);
	}
	
}
