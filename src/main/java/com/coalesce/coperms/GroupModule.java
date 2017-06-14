package com.coalesce.coperms;

import com.coalesce.config.IEntry;
import com.coalesce.config.ISection;
import com.coalesce.coperms.api.IGroup;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public final class GroupModule extends CoModule {
	
	private final CoPerms plugin;
	private final ISection mirrors;
	private final Collection<World> otherWorlds;
	//The world, the list of groups
	private final Map<String, Map<String, IGroup>> groups;
	private final Map<String, UserDataFile> userDataFiles;
	private final Map<String, GroupDataFile> groupDataFiles;
	private final Map<World, List<String>> hierarchy;
	private final List<CoWorld> resolved;
	private final List<World> queue;
	private final List<World> loaded;
	
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
		this.otherWorlds = new ArrayList<>();
		this.hierarchy = new HashMap<>();
		this.resolved = new ArrayList<>();
		this.loaded = new ArrayList<>();
		this.groups = new HashMap<>();
		this.queue = new ArrayList<>();
		this.plugin = plugin;
	}
	
	//All other worlds needs to be any worlds that weren't specified in the configuration.
	
	@Override
	protected void onEnable() throws Exception {
		mirrors.getKeys(false).forEach(System.out::println);
		Bukkit.getWorlds().forEach(w -> loadWorld(w));
	}
	
	public Map<String, UserDataFile> getUserDataFiles() {
		return userDataFiles;
	}
	
	public Map<String, GroupDataFile> getGroupDataFiles() {
		return groupDataFiles;
	}
	
	private void loadWorld(World world) {
		if (loaded.contains(world)) return;
		if (mirrors.contains(world.getName())) {
			System.out.println(world.getName() + " 1==================");
			IEntry e = mirrors.getEntry(world.getName());
			if (e.getStringList().contains("groups") && e.getStringList().contains("users")) {
				System.out.println(world.getName() + " 2==================");
				userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
				groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
				loaded.add(world);
				queue.remove(world);
			}
			else {
				if (e.getStringList().isEmpty()) {
					System.out.println(world.getName() + " 3==================");
					if (world.equals(Bukkit.getWorlds().get(0))) {
						throw new RuntimeException("Default world must have both the groups file and users file specified.");
					}
					userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					loaded.add(world);
					queue.remove(world);
					return;
				}
				System.out.println(world.getName() + " 4==================");
				e.getStringList().forEach(key -> {
					if (key.contains(":")) {
						System.out.println(world.getName() + " 5==================");
						String[] s = key.split(":");
						if (Bukkit.getWorld(s[0]) == null) {
							throw new RuntimeException("World " + s[0] + " does not exist.");
						}
						if (s[1].equalsIgnoreCase("users")) {
							System.out.println(world.getName() + " 6==================");
							if (userDataFiles.get(s[0]) == null) {
								System.out.println(world.getName() + " 7==================");
								this.queue.add(world);
								loadWorld(Bukkit.getWorld(s[0]));
							}
							else userDataFiles.put(world.getName(), userDataFiles.get(s[0]));
						}
						if (s[1].equalsIgnoreCase("groups")) {
							System.out.println(world.getName() + " 8==================");
							if (groupDataFiles.get(s[0]) == null) {
								System.out.println(world.getName() + " 9==================");
								this.queue.add(world);
								loadWorld(Bukkit.getWorld(s[0]));
							}
							else {
								System.out.println(world.getName() + " 10==================");
								groupDataFiles.put(world.getName(), groupDataFiles.get(s[0]));
							}
						}
						else {
							System.out.println(world.getName() + " 11==================");
							userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
							groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
							loaded.add(world);
							queue.remove(world);
							return;
						}
					}
					if (key.equalsIgnoreCase("groups")) {
						System.out.println(world.getName() + " 12==================");
						groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
					}
					if (key.equalsIgnoreCase("users")) {
						System.out.println(world.getName() + " 13==================");
						userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
					}
					else {
						System.out.println(world.getName() + " 14==================");
						userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
						groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
						loaded.add(world);
						queue.remove(world);
						return;
					}
				});
			}
		}
		else if (mirrors.contains("all-other-worlds")) {
			System.out.println(world.getName() + " 15==================");
			IEntry e = mirrors.getEntry("all-other-worlds");
			if (e.getStringList().isEmpty()) {
				System.out.println(world.getName() + " 16==================");
				userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
				groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
				loaded.add(world);
				queue.remove(world);
				return;
			}
			System.out.println(world.getName() + " 17==================");
			e.getStringList().forEach(key -> {
				System.out.println(world.getName() + " 18==================");
				if (key.contains(":")) {
					System.out.println(world.getName() + " 19==================");
					String[] s = key.split(":");
					if (Bukkit.getWorld(s[0]) == null) {
						throw new RuntimeException("World " + s[0] + " does not exist.");
					}
					if (s[1].equalsIgnoreCase("users")) {
						System.out.println(world.getName() + " 20==================");
						if (userDataFiles.get(s[0]) == null) {
							System.out.println(world.getName() + " 21==================");
							this.queue.add(world);
							loadWorld(Bukkit.getWorld(s[0]));
						}
						else {
							System.out.println(world.getName() + " 22==================");
							userDataFiles.put(world.getName(), userDataFiles.get(s[0]));
						}
					}
					if (s[1].equalsIgnoreCase("groups")) {
						System.out.println(world.getName() + " 23==================");
						if (groupDataFiles.get(s[0]) == null) {
							System.out.println(world.getName() + " 24==================");
							this.queue.add(world);
							loadWorld(Bukkit.getWorld(s[0]));
						}
						else {
							System.out.println(world.getName() + " 25==================");
							groupDataFiles.put(world.getName(), groupDataFiles.get(s[0]));
						}
					}
					else {
						System.out.println(world.getName() + " 26==================");
						userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
						groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
						loaded.add(world);
						queue.remove(world);
						return;
					}
				}
				if (key.equalsIgnoreCase("groups")) {
					System.out.println(world.getName() + " 27==================");
					groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
				}
				if (key.equalsIgnoreCase("users")) {
					System.out.println(world.getName() + " 28==================");
					userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
				}
				else {
					System.out.println(world.getName() + " 29==================");
					userDataFiles.put(world.getName(), userDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					groupDataFiles.put(world.getName(), groupDataFiles.get(Bukkit.getWorlds().get(0).getName()));
					loaded.add(world);
					queue.remove(world);
					return;
				}
			});
		}
		else {
			System.out.println(world.getName() + " 30==================");
			groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
			userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
			loaded.add(world);
			queue.remove(world);
		}
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
	}
	
}
