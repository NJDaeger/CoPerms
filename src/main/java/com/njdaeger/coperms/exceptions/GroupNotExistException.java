package com.njdaeger.coperms.exceptions;

import com.njdaeger.pdk.command.exception.PDKCommandException;

import static org.bukkit.ChatColor.RED;

public class GroupNotExistException extends PDKCommandException {
    
    public GroupNotExistException() {
        super(RED + "The group specified does not exist in the world specified");
    }
}
