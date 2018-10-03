package com.njdaeger.coperms.exceptions;

import com.njdaeger.bci.base.BCIException;

import static org.bukkit.ChatColor.RED;

public class GroupNotExistException extends BCIException {
    
    public GroupNotExistException() {
        super(RED + "The group specified does not exist in the world specified");
    }
}
