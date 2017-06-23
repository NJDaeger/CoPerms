package com.coalesce.coperms.exceptions;

public class GroupInheritMissing extends RuntimeException {
	
	public GroupInheritMissing() {
		super("The Group specified in the inherits section does not exist.");
	}
	
}
