package com.coalesce.coperms.configuration;

import com.coalesce.config.yml.YamlConfig;
import com.coalesce.coperms.CoPerms;
import com.coalesce.coperms.data.SuperGroup;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SuperDataFile extends YamlConfig {
	
	private final Set<SuperGroup> superGroups;
	
	public SuperDataFile(CoPerms plugin) {
		super("supergroups", plugin);
		this.superGroups = new HashSet<>();
		
		if (!contains("super", false)) {
			addEntry("super.default.permissions", Collections.singletonList("*"));
		}
		getSection("super").getKeys(false).forEach(key -> superGroups.add(new SuperGroup(key, getSection("super." + key))));
	}
	
	public Set<SuperGroup> getSuperGroups() {
		return superGroups;
	}
	
	
	
}
