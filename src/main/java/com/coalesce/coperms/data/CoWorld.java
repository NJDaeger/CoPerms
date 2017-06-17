package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public final class CoWorld {
	
	/*
	This will store the world, the groups in this world, the user file, the group file, a map of users
	 */
	
	private final World world;
	private final CoPerms plugin;
	private final Set<Group> groups;
	private final UserDataFile userData;
	private final GroupDataFile groupData;
	private final Map<UUID, CoUser> users;
	
	public CoWorld(CoPerms plugin, World world, UserDataFile userdata, GroupDataFile groupdata) {
		this.world = world;
		this.plugin = plugin;
		this.userData = userdata;
		this.groupData = groupdata;
		this.users = new HashMap<>();
		this.groups = new HashSet<>();
		
		groupdata.getSection("groups").getKeys(false).forEach(key -> groups.add(new Group(plugin, this, key)));
	}
}
