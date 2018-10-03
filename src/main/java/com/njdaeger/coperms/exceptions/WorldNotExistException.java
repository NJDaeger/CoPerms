package com.njdaeger.coperms.exceptions;

import com.njdaeger.bci.base.BCIException;

import static org.bukkit.ChatColor.RED;

public class WorldNotExistException extends BCIException {
    
    public WorldNotExistException() {
        super(RED + "The world specified does not exist.");
    }
}
