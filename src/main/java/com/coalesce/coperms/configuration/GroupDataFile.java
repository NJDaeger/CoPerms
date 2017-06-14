package com.coalesce.coperms.configuration;

import com.coalesce.config.yml.YamlConfig;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.api.IGroup;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class GroupDataFile extends YamlConfig {
	
	
	private final CoPerms plugin;
	private final World world;
	
	public GroupDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "groups", plugin);
		
		this.plugin = plugin;
		this.world = world;
		
		List<String> permissions = new ArrayList<>();
		permissions.add("coperms.test");
		permissions.add("coperms.test2");
		permissions.add("coperms.test3");
		
		if (!contains("groups", false)) {
			addEntry("groups.default.position", 1);
			addEntry("groups.default.permissions", permissions);
			addEntry("groups.default.inherits", "");
			addEntry("groups.default.info.canBuild", true);
			addEntry("groups.default.info.prefix", "");
			addEntry("groups.default.info.suffix", "");
		}
	}
	
	public IGroup getDefaultGroup() {
		getEntries().forEach(e -> e.getInt()); //TODO: Create a getParent section in ISection then start loading data for the users.
		return null;
	}
	
}
