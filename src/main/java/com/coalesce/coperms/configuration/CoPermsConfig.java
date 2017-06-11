package com.coalesce.coperms.configuration;

import com.coalesce.config.yml.YamlConfig;
import com.coalesce.plugin.CoPlugin;

public final class CoPermsConfig extends YamlConfig {
	
	public CoPermsConfig(CoPlugin plugin) {
		super("config", plugin);
		
		addEntry("test.this.config", true);
		addEntry("test.this.config2", false);
		addEntry("test.this2.config", true);
		addEntry("test.this2.config2", false);
	}
	
}
