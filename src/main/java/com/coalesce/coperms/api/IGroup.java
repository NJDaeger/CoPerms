package com.coalesce.coperms.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IGroup {
	
	String getName();
	
	List<UUID> getPlayers();
	
	void addPlayer(Player player);
	
	void addPlayer(UUID uuid);
	
	void removePlayer(Player player);
	
	void removePlayer(UUID uuid);
	
	List<String> getPermissions();
	
	void addPermission(String permission);
	
	void removePermission(String permission);
	
	boolean hasPermission(String permission);
	
/*	String getPrefix();
	
	void setPrefix(String prefix);
	
	String getSuffix();
	
	void setSuffix();
	
	IGroup[] getInheritedGroups();
	
	void addInheritedGroup(IGroup group);
	
	void removeInheritedGroup();*/
	
}
