package com.njdaeger.coperms.exceptions;

import com.njdaeger.pdk.command.exception.PDKCommandException;

import static org.bukkit.ChatColor.RED;

public class WorldNotExistException extends PDKCommandException {
    
    public WorldNotExistException() {
        super(RED + "The world specified does not exist.");
    }
}
