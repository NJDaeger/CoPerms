package com.coalesce.coperms.data;

import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import org.bukkit.World;

public final class CoWorld {
	
	public final World world;
	public final UserDataFile userData;
	public final GroupDataFile groupData;
	
	public CoWorld(World world, UserDataFile userdata, GroupDataFile groupdata) {
		this.world = world;
		this.userData = userdata;
		this.groupData = groupdata;
	}
	
	public World getWorld() {
		return world;
	}
	
	public UserDataFile getUserData() {
		return userData;
	}
	
	public GroupDataFile getGroupData() {
		return groupData;
	}
}
