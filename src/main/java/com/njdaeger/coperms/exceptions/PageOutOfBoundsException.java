package com.njdaeger.coperms.exceptions;

import com.njdaeger.bci.base.BCIException;

import static org.bukkit.ChatColor.RED;

public class PageOutOfBoundsException extends BCIException {

    public PageOutOfBoundsException() {
        super(RED + "The page you provided is out of bounds. (Does not exist!)");
    }

}
