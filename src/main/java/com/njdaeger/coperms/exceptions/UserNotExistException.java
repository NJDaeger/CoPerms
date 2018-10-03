package com.njdaeger.coperms.exceptions;

import com.njdaeger.bci.base.BCIException;

import static org.bukkit.ChatColor.RED;

public class UserNotExistException extends BCIException {
    
    public UserNotExistException() {
        super(RED + "The user specified does not exist in the world specified");
    }
}
