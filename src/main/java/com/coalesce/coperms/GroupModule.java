package com.coalesce.coperms;

import com.coalesce.config.IEntry;
import com.coalesce.config.ISection;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GroupModule extends CoModule {
	
	private final CoPerms plugin;
	private final ISection mirrors;
	private final Map<String, UserDataFile> userDataFiles;
	private final Map<String, GroupDataFile> groupDataFiles;
	private final List<World> queue;
	private final List<World> loaded;
	private final List<CoWorld> worlds;
	
	/**
	 * Create a new module
	 *
	 * @param plugin The plugin that's creating this module
	 */
	public GroupModule(CoPerms plugin) {
		super(plugin, "Group Loader Module");
		this.mirrors = plugin.getPermsConfig().getMirrors();
		this.groupDataFiles = new HashMap<>();
		this.userDataFiles = new HashMap<>();
		this.loaded = new ArrayList<>();
		this.worlds = new ArrayList<>();
		this.queue = new ArrayList<>();
		this.plugin = plugin;
	}
	
	//All other worlds needs to be any worlds that weren't specified in the configuration.
	
	@Override
	protected void onEnable() throws Exception {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			for (World world : Bukkit.getWorlds()) {
				loadData(world);
			}
			for (World w : queue) {
				loadOtherWorlds(w);
			}
			for (World world : loaded) {
				worlds.add(new CoWorld(world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
			}
		});
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	/**
	 * Gets a list of CoPerms' worlds.
	 * @return A list of CoWorlds
	 */
	public List<CoWorld> getWorlds() {
		return worlds;
	}
	
	private void loadData(World world) {
		
		//Mirrors must contain the default world
		if (mirrors.contains(world.getName())) {
			IEntry e = mirrors.getEntry(world.getName());
			
			//if the world in mirrors has both the groups and users string, then load the datafiles.
			if (e.getStringList().contains("groups") && e.getStringList().contains("users")) {
				userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
				groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
				loaded.add(world);
			}
			
			//we need to resolve what else is contained in this datafile
			else {
				if (e.getStringList() == null) {
					if (world.equals(Bukkit.getWorlds().get(0))) {
						throw new RuntimeException("Default world must have both the groups file and users file specified.");
					}
					userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					loaded.add(world);
					return;
				}
				loadKeys(e.getStringList(), world);
			}
		}
		else queue.add(world);
	}
	
	private void loadOtherWorlds(World world) {
		//If the mirrors section of the config contains "all-other-worlds" then we will load the keys and generate files for each world.
		if (mirrors.contains("all-other-worlds")) {
			loadKeys(mirrors.getEntry("all-other-worlds").getStringList(), world);
			return;
		}
		
		//All other worlds will get their data from the default world.
		userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
		groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
		loaded.add(world);
	}
	
	private void loadKeys(List<String> keys, World world) {
		//Lets go through each key in this list.
		keys.forEach(key -> {
			
			//If even one key matches a loaded world name, then the loop stops and both datafiles are used from the found world
			if (loaded.contains(Bukkit.getWorld(key))) {
				groupDataFiles.put(world.getName(), groupDataFiles.get(key));
				userDataFiles.put(world.getName(), userDataFiles.get(key));
				return;
			}
			
			//If a key equals "groups" then it creates a new groupfile for this world
			if (key.equalsIgnoreCase("groups")) {
				groupDataFiles.put(world.getName(), groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world)));
			}
			
			//If a key equals "users" then it creates a new userfile for this world
			if (key.equalsIgnoreCase("users")) {
				userDataFiles.put(world.getName(), userDataFiles.put(world.getName(), new UserDataFile(plugin, world)));
			}
			
			//If the key contains a colon, then we know the key means its getting data from elsewhere.
			if (key.contains(":")) {
				String[] s = key.split(":");
				if (Bukkit.getWorld(s[0]) == null) {
					throw new RuntimeException("World " + s[0] + " does not exist.");
				}
				
				//Gets the user file from the specified world
				if (s[1].equalsIgnoreCase("users")) {
					userDataFiles.put(world.getName(), userDataFiles.get(s[0]));
				}
				
				//Gets the group file from the specified world
				if (s[1].equalsIgnoreCase("groups")) {
					groupDataFiles.put(world.getName(), groupDataFiles.get(s[0]));
				}
				
			}
		});
		
		//We need to load something, so we load the default worlds data.
		userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
		groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
		loaded.add(world);
	}
	
}
