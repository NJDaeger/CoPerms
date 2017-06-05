package com.coalesce.coperms;

import com.coalesce.coperms.configuration.Configuration;
import com.coalesce.plugin.CoPlugin;

public final class CoPerms extends CoPlugin {
	
	@Override
	public void onPluginEnable() {
		this.updateCheck("Project-Coalesce", "CoPerms", true);
		
		new Configuration(this);
		
	}
	
	@Override
	public void onPluginDisable() {
	
	}
}
