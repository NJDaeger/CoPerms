package com.coalesce.coperms.data;

import com.coalesce.coperms.CoPerms;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class Group {
	
	private final Set<String> permissions;
	private final Set<UUID> users;
	private final CoPerms plugin;
	private final CoWorld world;
	private final String name;
	
	
	public Group(CoPerms plugin, CoWorld world, String name) {
		this.permissions = new HashSet<>();
		this.users = new HashSet<>();
		this.plugin = plugin;
		this.world = world;
		this.name = name;
	}
}
