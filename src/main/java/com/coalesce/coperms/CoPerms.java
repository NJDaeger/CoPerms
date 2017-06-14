package com.coalesce.coperms;

import com.coalesce.coperms.configuration.CoPermsConfig;
import com.coalesce.plugin.CoPlugin;

public final class CoPerms extends CoPlugin {
	
	private GroupModule groupModule;
	private CoPermsConfig config;
	
	@Override
	public void onPluginEnable() {
		this.updateCheck("Project-Coalesce", "CoPerms", true);
		
		this.config = new CoPermsConfig(this);
		
		addModules(
				this.groupModule = new GroupModule(this));
		
	}
	
	@Override
	public void onPluginDisable() {
	
	}
	
	/**
	 * Gets the groups module.
	 * @return The groups module.
	 */
	public GroupModule getGroupModule() {
		return groupModule;
	}
	
	/**
	 * Gets CoPerms' configuration.
	 * @return CoPerm's configuration.
	 */
	public CoPermsConfig getPermsConfig() {
		return config;
	}
	
}
