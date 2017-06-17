package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class CoUser {
	
	/*
	This will store the users group, current world, user ID, the user section, all the permissions the user has.
	 */
	private Group group;
	private CoWorld world;
	private final UUID uuid;
	private final CoPerms plugin;
	private final ISection userSection;
	private final Set<String> permissions;
	
	public CoUser(CoPerms plugin, CoWorld world, Group group, ISection userSection, UUID userID) {
		this.permissions = new HashSet<>();
		this.userSection = userSection;
		this.plugin = plugin;
		this.world = world;
		this.group = group;
		this.uuid = userID;
	}
}
