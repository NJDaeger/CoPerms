package com.njdaeger.coperms.exceptions;

public class InheritanceParseException extends RuntimeException {
    
    public InheritanceParseException(String key, String group) {
        super("Unable to parse key \"" + key + "\" for group \"" + group + "\"");
    }
    
}
