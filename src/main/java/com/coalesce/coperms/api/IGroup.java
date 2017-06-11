package com.coalesce.coperms.api;

import com.coalesce.coperms.configuration.GroupDataFile;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IGroup {
	
	/**
	 * Gets the name of this group.
	 * @return The name of this group.
	 */
	String getName();
	
	/**
	 * Gets a list of all the players that are currently in this group.
	 * @return The list of players in this group.
	 */
	List<UUID> getPlayers();
	
	/**
	 * Adds a player to add to this group.
	 * @param uuid The player UUID to add.
	 */
	void addPlayer(UUID uuid);
	
	/**
	 * The player to remove from this group.
	 * @param uuid The player UUID to remove.
	 */
	void removePlayer(UUID uuid);
	
	/**
	 * Gets a list of permissions for this group
	 * @return The permissions for this group.
	 */
	List<String> getPermissions();
	
	/**
	 * Adds a permission to this group.
	 * @param permission The permission node to add.<p>
	 *
	 * <p><b> Note: If you want to remove a permission from a wildcard node do this: </b><p>
	 *
	 *                permissions:<br>
	 *                   - wildcard.*<br>
	 *                   - -wildcard.permission.inside.wildcard
	 */
	void addPermission(String permission);
	
	/**
	 * Removes a permission from a group
	 * @param permission The permission node to remove.
	 */
	void removePermission(String permission);
	
	/**
	 * Checks if the group has a permission.
	 * @param permission The permission to look for.
	 * @return True if the group has the permission, false otherwise.
	 */
	boolean hasPermission(String permission);
	
	/**
	 * Gets the datafile that contains the group data.
	 * @return The group datafile.
	 */
	GroupDataFile getDataFile();
	
/*	String getPrefix();
	
	void setPrefix(String prefix);
	
	String getSuffix();
	
	void setSuffix();
	
	IGroup[] getInheritedGroups();
	
	void addInheritedGroup(IGroup group);
	
	void removeInheritedGroup();*/
	
}
