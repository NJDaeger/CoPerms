package com.coalesce.coperms.exceptions;

import com.coalesce.core.Color;

public class DefaultGroupMissing extends RuntimeException {
    
    public DefaultGroupMissing(String world) {
        super(Color.BOLD.toString() + Color.RED + "There is no group ID '0' for world \"" + world + "\".");
        System.out.println(Color.GRAY + "[" + Color.AQUA + "CoPerms" + Color.GRAY + "]" + Color.GOLD +
                " This error can be fixed by going into " + world + "'s group data folder and making" +
                " a group ID 0.");
    }
    
}
