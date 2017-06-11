package com.coalesce.coperms;

import com.coalesce.coperms.api.IGroup;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.coperms.data.Group;
import com.coalesce.plugin.CoModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class GroupModule extends CoModule {
	
	private final CoPerms plugin;
	private final Map<String, IGroup> groups;
	private final Map<String, UserDataFile> userDataFiles;
	private final Map<String, GroupDataFile> groupDataFiles;
	
	/**
	 * Create a new module
	 *
	 * @param plugin The plugin that's creating this module
	 */
	public GroupModule(CoPerms plugin) {
		super(plugin, "Group Loader Module");
		this.groupDataFiles = new HashMap<>();
		this.userDataFiles = new HashMap<>();
		this.groups = new HashMap<>();
		this.plugin = plugin;
	}
	
	@Override
	protected void onEnable() throws Exception {
		for (World world : Bukkit.getWorlds()) {
			groupDataFiles.put(world.getName(), new GroupDataFile(plugin, world));
			userDataFiles.put(world.getName(), new UserDataFile(plugin, world));
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
			groupDataFiles.forEach((world, file) ->
				file.getSection("groups").getKeys(false).forEach(key ->
					new Group(key, file, plugin))
			)
		);
	}
	
	/**
	 * Gets a group from a string
	 * @param name The name of the group
	 * @return The group specified from the string.
	 */
	public IGroup getGroup(String name) {
		return groups.get(name);
	}
	
	public UserDataFile getUserDataFile(String world) {
		return userDataFiles.get(world);
	}
	
	public GroupDataFile getGroupDataFile(String world) {
		return groupDataFiles.get(world);
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		getUserDataFile(e.getPlayer().getWorld().getName()).loadUser(e.getPlayer());
	}
	
}
