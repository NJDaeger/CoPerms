package com.coalesce.coperms.data;

import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.coperms.configuration.UserDataFile;
import com.coalesce.plugin.CoPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Group {
	
	private final List<String> permissions;
	private final GroupDataFile dataFile;
	private final UserDataFile userData;
	private final List<UUID> players;
	private final CoPlugin plugin;
	private final String name;
	
	public Group(String name, GroupDataFile dataFile, UserDataFile userData, CoPlugin plugin) {
		this.permissions = new ArrayList<>();
		this.players = new ArrayList<>();
		this.dataFile = dataFile;
		this.userData = userData;
		this.plugin = plugin;
		this.name = name;
		
	}
	
	public String getName() {
		return name;
	}
	
	public List<UUID> getPlayers() {
		return players;
	}
	
	public void addPlayer(UUID uuid) {
		players.add(uuid);
	}
	
	public void removePlayer(UUID uuid) {
		players.remove(uuid);
	}
	
	public List<String> getPermissions() {
		return permissions;
	}
	
	public void addPermission(String permission) {
		permissions.add(permission);
	}
	
	public void removePermission(String permission) {
		permissions.remove(permission);
	}
	
	public boolean hasPermission(String permission) {
		return permissions.contains(permission);
	}
	
	public GroupDataFile getDataFile() {
		return dataFile;
	}
	
	public boolean isDefaultGroup() {
		return dataFile.getBoolean("groups." + name + ".default");
	}
	
	//TODO: This stuff will be added in later on.
/*
	@Override
	public String getPrefix() {
		return null;
	}
	
	@Override
	public void setPrefix(String prefix) {
	
	}
	
	@Override
	public String getSuffix() {
		return null;
	}
	
	@Override
	public void setSuffix() {
	
	}
	
	@Override
	public IGroup[] getInheritedGroups() {
		return new IGroup[0];
	}
	
	@Override
	public void addInheritedGroup(IGroup group) {
	
	}
	
	@Override
	public void removeInheritedGroup() {
	
	}*/
}
