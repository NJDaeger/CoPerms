package com.coalesce.coperms.configuration;

import com.coalesce.config.ISection;
import com.coalesce.config.yml.YamlConfig;
import com.coalesce.coperms.CoPerms;
import org.bukkit.Bukkit;

import java.util.Arrays;

public final class CoPermsConfig extends YamlConfig {
	
	public CoPermsConfig(CoPerms plugin) {
		super("config", plugin);
		
		addEntry("allow-manual-promotion", false);
		addEntry("operator-overrides", true);
		addEntry("log-commands", true);
		//structure.worlds.WORLDNAME.WORLDS
		//all other worlds is specifying that all the other worlds not in this section will inherit the users and groups from this world
		if (getMirrors() == null) {
			addEntry("mirrors." + Bukkit.getWorlds().get(0).getName(), Arrays.asList("users", "groups")); //Check if this
			addEntry("mirrors.all-other-worlds", Arrays.asList("users", "groups"));
		}
		/*
		
		world:
			- users
			- groups
		world_nether: #This would get its data from the world section (above)
			- world
		world_the_end #This would use the world section groups file, but contain its own user file
			- world:groups
			- users
		world1: #This would use the world section users file, but have its own groups file
			- world:users
			- groups
		world1_nether: #This would get its data from the server's default world section
		world1_the_end: #This would get its users file from world1's users file (which is from world)
			- world1:users
		world2: #This would have its own groups file but it would get its users file from the server's default world.
			- groups
		all-other-worlds: #Any other worlds that weren't specified in this list will get their data from the world section
			- world
		
		
		 */
	}
	
	public boolean allowManualPromotion() {
		return getBoolean("allow-manual-promotion");
	}
	
	public boolean allowOperatorOverrides() {
		return getBoolean("operator-overrides");
	}
	
	public boolean logCommands() {
		return getBoolean("log-commands");
	}
	
	public ISection getMirrors() {
		return getSection("mirrors");
	}
	
}
