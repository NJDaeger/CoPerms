package com.coalesce.coperms.exceptions;

public class SuperGroupMissing extends RuntimeException {

    public SuperGroupMissing() {
        super("The SuperGroup specified in the inherit section does not exist.");
    }

}
