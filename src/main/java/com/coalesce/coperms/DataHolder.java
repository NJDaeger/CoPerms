package com.coalesce.coperms;

import com.coalesce.coperms.data.CoUser;
import com.coalesce.coperms.data.CoWorld;
import com.coalesce.coperms.data.Group;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.*;

public final class DataHolder {
	
	/*
	this needs to hold all the worlds, the users, and the groups
	 */
	private final DataLoader data;
	private final Set<Group> groups;
	private final Map<UUID, CoUser> users;
	private final Map<String, CoWorld> worlds;
	
	public DataHolder(DataLoader dataloader) {
		this.data = dataloader;
		this.users = new HashMap<>();
		this.worlds = new HashMap<>();
		this.groups = new HashSet<>();
	}
	
}
