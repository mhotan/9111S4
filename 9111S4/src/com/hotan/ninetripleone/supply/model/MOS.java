package com.hotan.ninetripleone.supply.model;

/**
 * Enum type that represents different types 
 * of MOS with a team.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 *
 */
public enum MOS {

    ALPHA("18A"),
    WARRENT("18A0"),
    BRAVO("18B"),
    CHARLIE("18C"),
    DELTA("18D"),
    ECHO("18E"),
    FOX("18F"),
    ZULU("18Z")
    ;
    
    private final String mName;
    
    MOS(String name) {
        mName = name;
    }
    
    @Override
    public String toString() {
        return mName;
    }
}
