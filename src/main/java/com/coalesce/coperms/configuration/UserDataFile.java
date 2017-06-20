package com.coalesce.coperms.configuration;

import com.coalesce.config.yml.YamlConfig;
import com.coalesce.coperms.CoPerms;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Collections;
import java.util.UUID;

public final class UserDataFile extends YamlConfig {
	
	private volatile CoPerms plugin;
	private final World world;
	
	public UserDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "users", plugin);
		
		this.plugin = plugin;
		this.world = world;
		
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.username", "NJDaeger");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.group", "default");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.prefix", "");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.suffix", "");
		addEntry("users.17b65a67-f96e-425c-a184-477f067c53f9.permissions", Collections.singletonList("*"));
	}
	
	/**
	 * Loads a user to this world via uuid
	 * @param uuid The user to load
	 * @return The loaded user.
	 */
	public void loadUser(UUID uuid) {
		String path = "users." + uuid.toString();
		setEntry(path + ".username", Bukkit.getPlayer(uuid).getName());
		addEntry(path + ".group", plugin.getDataHolder().getWorld(world).getDefaultGroup().getName());
		addEntry(path + ".prefix", "");
		addEntry(path + ".suffix", "");
		addEntry(path + ".permissions", Collections.emptyList());
	}
}
