package com.njdaeger.coperms.exceptions;

public class GroupInheritMissing extends RuntimeException {

    public GroupInheritMissing(String key) {
        super("The Group specified in the inherits section does not exist. " + "\"" + key + "\"");
    }

}
