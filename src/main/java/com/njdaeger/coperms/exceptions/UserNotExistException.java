package com.njdaeger.coperms.exceptions;

import com.njdaeger.pdk.command.exception.PDKCommandException;

import static org.bukkit.ChatColor.RED;

public class UserNotExistException extends PDKCommandException {
    
    public UserNotExistException() {
        super(RED + "The user specified does not exist in the world specified");
    }
}
