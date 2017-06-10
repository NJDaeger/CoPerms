package com.coalesce.coperms;

import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.data.Group;
import com.coalesce.plugin.CoModule;
import com.coalesce.plugin.CoPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;

import java.util.*;

public final class GroupModule extends CoModule {
	
	private final CoPlugin plugin;
	private final Map<String, Group> groups;
	private final Collection<GroupDataFile> groupData;
	
	/**
	 * Create a new module
	 *
	 * @param plugin The plugin that's creating this module
	 */
	public GroupModule(CoPlugin plugin) {
		super(plugin, "Group Loader Module");
		this.groupData = new ArrayList<>();
		this.groups = new HashMap<>();
		this.plugin = plugin;
	}
	
	@Override
	protected void onEnable() throws Exception {
		for (World world : Bukkit.getWorlds()) {
			groupData.add(new GroupDataFile(plugin, world));
		}
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
			groupData.forEach(file ->
				file.getSection("groups").getKeys(false).forEach(key ->
					new Group(key, file, plugin))
			)
		);
	}
	
	@Override
	protected void onDisable() throws Exception {
	
	}
	
	
	
}
