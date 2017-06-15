package com.coalesce.coperms.configuration;

import com.coalesce.config.yml.YamlConfig;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.Group;
import org.bukkit.World;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GroupDataFile extends YamlConfig {
	
	private final List<Group> groups;
	private final CoPerms plugin;
	private final World world;
	
	public GroupDataFile(CoPerms plugin, World world) {
		super("worlds" + File.separator + world.getName() + File.separator + "groups", plugin);
		
		this.groups = new ArrayList<>();
		this.plugin = plugin;
		this.world = world;
		
		if (!contains("groups", false)) {
			addEntry("groups.default.default", true);
			addEntry("groups.default.permissions", Arrays.asList("ttb.generate", "ttb.undo", "ttb.redo"));
			addEntry("groups.default.inherits", "");
			addEntry("groups.default.info.canBuild", true);
			addEntry("groups.default.info.prefix", "");
			addEntry("groups.default.info.suffix", "");
		}
	}
	
	/**
	 *
	 * @return
	 */
	public Group getDefaultGroup() {
		for (String group : getSection("groups").getKeys(false)) {
			if (getSection("groups." + group).getEntry("ladder").getInt() == 0) {
				return getGroup(group);
			}
		}
		return null;
	}
	
	public List<Group> getGroups() {
		return groups;
	}
	
	public Group getGroup(String name) {
		for (Group group : groups) {
			if (group.getName().equalsIgnoreCase(name)) {
				return group;
			}
		}
		return null;
	}
	
}
