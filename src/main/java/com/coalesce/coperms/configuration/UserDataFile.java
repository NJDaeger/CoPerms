package com.coalesce.coperms.configuration;

import com.coalesce.coperms.CoPerms;
import com.coalesce.core.config.YmlConfig;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.Collections;
import java.util.UUID;

public final class UserDataFile extends YmlConfig {
	
	private final CoPerms plugin;
	private final World world;
	
	public UserDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "users", plugin);
		
		this.plugin = plugin;
		this.world = world;
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
		addEntry(path + ".info.prefix", null);
		addEntry(path + ".info.suffix", null);
		addEntry(path + ".permissions", Collections.emptyList());
	}
}
