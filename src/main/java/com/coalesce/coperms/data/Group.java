package com.coalesce.coperms.data;

import com.coalesce.coperms.api.IGroup;
import com.coalesce.coperms.configuration.GroupDataFile;
import com.coalesce.plugin.CoPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Group implements IGroup {
	
	private final List<String> permissions;
	private final GroupDataFile dataFile;
	private final List<UUID> players;
	private final CoPlugin plugin;
	private final String name;
	
	public Group(String name, GroupDataFile dataFile, CoPlugin plugin) {
		this.permissions = new ArrayList<>();
		this.players = new ArrayList<>();
		this.dataFile = dataFile;
		this.plugin = plugin;
		this.name = name;
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<UUID> getPlayers() {
		return players;
	}
	
	@Override
	public void addPlayer(UUID uuid) {
		players.add(uuid);
	}
	
	@Override
	public void removePlayer(UUID uuid) {
		players.remove(uuid);
	}
	
	@Override
	public List<String> getPermissions() {
		return permissions;
	}
	
	@Override
	public void addPermission(String permission) {
		permissions.add(permission);
	}
	
	@Override
	public void removePermission(String permission) {
		permissions.remove(permission);
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return permissions.contains(permission);
	}
	
	@Override
	public GroupDataFile getDataFile() {
		return dataFile;
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
