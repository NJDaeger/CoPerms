package com.coalesce.coperms.data;

import com.coalesce.config.ISection;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.api.IGroup;
import org.bukkit.entity.Player;

public final class CoUser {
	
	private final Player player;
	private final CoPerms plugin;
	private final ISection userSection;
	
	public CoUser(CoPerms plugin, Player player, ISection userSection) {
		this.userSection = userSection;
		this.plugin = plugin;
		this.player = player;
	}
	
	public ISection getUserSection() {
		return userSection;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public IGroup getGroup() {
		return null;
	}
}
