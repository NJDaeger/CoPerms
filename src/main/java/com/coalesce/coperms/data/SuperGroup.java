package com.coalesce.coperms.data;

import com.coalesce.config.ISection;

import java.util.HashSet;
import java.util.Set;

public final class SuperGroup {
	
	private final String name;
	private final Set<String> permissions;
	
	public SuperGroup(String name, ISection section) {
		this.permissions = new HashSet<>(section.getEntry("permissions").getStringList());
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<String> getPermissions() {
		return permissions;
	}
	
}
