package com.coalesce.coperms.configuration;

import com.coalesce.config.json.JsonConfig;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.CoUser;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.*;

public final class UserDataFile extends JsonConfig {
	
	private final Set<CoUser> users;
	private final CoPerms plugin;
	private final World world;
	
	public UserDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "users", plugin);
		
		this.users = new HashSet<>();
		this.plugin = plugin;
		this.world = world;
		
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.username", "NJDaeger");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.group", "default");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.prefix", "");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.suffix", "");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.permissions", Collections.singletonList("*"));
	}
	
	/**
	 * Gets a user from this datafile.
	 * @param uuid The user to obtain. (Offline or online)
	 * @return The user
	 *
	 */
	public CoUser getUser(UUID uuid) {
		for (CoUser user : users) {
			if (user.getUuid().equals(uuid)) return user;
		}
		return null;
	}
	
	
	/**
	 * Loads a user to this world via uuid
	 * @param uuid The user to load
	 * @return The loaded user.
	 */
	public CoUser loadUser(UUID uuid) {
		if (getUser(uuid) != null) return getUser(uuid);
		String path = "users." + uuid.toString();
		setEntry(path + ".username", Bukkit.getPlayer(uuid).getName());
		addEntry(path + ".group", plugin.getDataHolder().getWorld(world).getDefaultGroup().getName());
		addEntry(path + ".prefix", "");
		addEntry(path + ".suffix", "");
		addEntry(path + ".permissions", Collections.emptyList());
		return new CoUser(plugin, uuid, getSection(path), plugin.getDataHolder().getWorld(Bukkit.getPlayer(uuid).getWorld()), plugin.getDataHolder().getGroup(getString(path + ".group")));
	}
	
	/**
	 * Unloads a user from this world.
	 * @param uuid The user to unload.
	 */
	public void unloadUser(UUID uuid) {
		setEntry("users." + uuid.toString() + ".username", Bukkit.getPlayer(uuid).getName());
		addEntry("users." + uuid.toString() + ".group", plugin.getDataHolder().getWorld(world).getDefaultGroup().getName());
		addEntry("users." + uuid.toString() + ".prefix", "");
		addEntry("users." + uuid.toString() + ".suffix", "");
		addEntry("users." + uuid.toString() + ".permissions", Collections.emptyList());
		users.remove(getUser(uuid));
	}
}