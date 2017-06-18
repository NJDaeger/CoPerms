package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CoWorld {
	
	/*
	This will store the world, the groups in this world, the user file, the group file, a map of users
	 */
	
	private final World world;
	private final CoPerms plugin;
	private final UserDataFile userData;
	private final GroupDataFile groupData;
	private final Map<UUID, CoUser> users;
	private final Map<String, Group> groups;
	
	public CoWorld(CoPerms plugin, World world, UserDataFile userData, GroupDataFile groupData) {
		this.world = world;
		this.plugin = plugin;
		this.userData = userData;
		this.groupData = groupData;
		this.users = new HashMap<>();
		this.groups = new HashMap<>();
		
		groupData.getSection("groups").getKeys(false).forEach(key -> groups.put(key, new Group(plugin, this, key)));
	}
	
	
}
