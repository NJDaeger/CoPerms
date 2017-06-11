package com.coalesce.coperms.configuration;

import com.coalesce.config.json.JsonConfig;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.CoUser;
import com.coalesce.plugin.CoPlugin;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserDataFile extends JsonConfig {
	
	private final CoPerms plugin;
	
	public UserDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "users", plugin);
		
		this.plugin = plugin;
		
	}
	
	public CoUser loadUser(Player player) {
		setEntry("users." + player.getUniqueId().toString() + ".name", player.getName());
		addEntry("users." + player.getUniqueId().toString() + ".group", plugin.getGroupModule().getDefaultGroup().getName());
		return new CoUser(player, getSection("users." + player.getUniqueId().toString()));
	}
	
}
