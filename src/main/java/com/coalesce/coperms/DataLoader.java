package com.coalesce.coperms;

import com.coalesce.config.IEntry;
import com.coalesce.config.ISection;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class DataLoader extends CoModule {
	
	private final Map<String, GroupDataFile> groupDataFiles;
	private final Map<String, UserDataFile> userDataFiles;
	private final Map<String, CoWorld> worlds;
	private final Set<World> loaded;
	private final Set<World> queue;
	private final ISection mirrors;
	private DataHolder dataHolder;
	private final CoPerms plugin;
	private final String def;
	
	/**
	 * Create a new module
	 *
	 * @param plugin The plugin that's creating this module
	 */
	public DataLoader(CoPerms plugin) {
		super(plugin, "Data Loader");
		
		this.mirrors = plugin.getPermsConfig().getSection("mirrors");
		this.def = Bukkit.getWorlds().get(0).getName();
		this.groupDataFiles = new HashMap<>();
		this.userDataFiles = new HashMap<>();
		this.worlds = new HashMap<>();
		this.loaded = new HashSet<>();
		this.queue = new HashSet<>();
		this.plugin = plugin;
	}
	
	//All other worlds needs to be any worlds that weren't specified in the configuration.
	
	@Override
	protected void onEnable() throws Exception {
		for (World world : Bukkit.getWorlds()) {
			loadData(world);
		}
		for (World world : queue) {
			loadOtherWorlds(world);
		}
		for (World world : loaded) {
			worlds.put(world.getName(), new CoWorld(plugin, world, userDataFiles.get(world.getName()), groupDataFiles.get(world.getName())));
		}
		this.dataHolder = new DataHolder(this);
		new DataListener(dataHolder, plugin);
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	/**
	 * Gets the data holder for the plugin.
	 * @return The data folder.
	 */
	protected DataHolder getDataHolder() {
		return dataHolder;
	}
	
	/**
	 * Gets the loaded worlds
	 * @return The loaded worlds
	 */
	protected Map<String, CoWorld> getWorlds() {
		return worlds;
	}
	
	/**
	 * Loads the data from a world that is contained in the mirrors configuration section.
	 * @param world The world to load the data from.
	 */
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
					userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(def));
					groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(def));
					loaded.add(world);
					return;
				}
				loadKeys(e.getStringList(), world);
			}
		}
		else queue.add(world);
	}
	
	/**
	 * Loads the "all-other-worlds" key in the mirrors config section.
	 * @param world A world from the queue list.
	 */
	private void loadOtherWorlds(World world) {
		//If the mirrors section of the config contains "all-other-worlds" then we will load the keys and generate files for each world.
		if (mirrors.contains("all-other-worlds")) {
			loadKeys(mirrors.getEntry("all-other-worlds").getStringList(), world);
			return;
		}
		
		//All other worlds will get their data from the default world.
		userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(def));
		groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(def));
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
		userDataFiles.putIfAbsent(world.getName(), userDataFiles.get(def));
		groupDataFiles.putIfAbsent(world.getName(), groupDataFiles.get(def));
		loaded.add(world);
	}
	
}
