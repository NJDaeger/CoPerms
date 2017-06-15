package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;

import java.util.UUID;

public final class CoUser {
	
	private final UUID uuid;
	private final CoPerms plugin;
	private final ISection userSection;
	
	public CoUser(CoPerms plugin, UUID userID, ISection userSection) {
		this.userSection = userSection;
		this.plugin = plugin;
		this.uuid = userID;
	}
	
	public ISection getUserSection() {
		return userSection;
	}
	
	public UUID getUuid() {
		return uuid;
	}
	
	public Group getGroup() {
		return plugin.getDataHolder().getGroup(userSection.getEntry("group").getString());
	}
	
	public String getGroupName() {
		return userSection.getEntry("group").getString();
	}
	
	public void setGroup(String name) {
	
	}
	
	
	
}
