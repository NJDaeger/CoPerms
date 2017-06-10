package com.coalesce.coperms;

import com.coalesce.coperms.configuration.Configuration;
import com.coalesce.coperms.data.Group;
import com.coalesce.plugin.CoPlugin;
import org.bukkit.Bukkit;

public final class CoPerms extends CoPlugin {
	
	private GroupModule groupModule;
	
	@Override
	public void onPluginEnable() {
		this.updateCheck("Project-Coalesce", "CoPerms", true);
		
		addModules(
				groupModule = new GroupModule(this));
		
		new Configuration(this);
		
	}
	
	@Override
	public void onPluginDisable() {
	
	}
}
