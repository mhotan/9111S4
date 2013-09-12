package com.hotan.ninetripleone.supply.model;

/**
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public enum Rank {
    
    PVT("PVT"), PV2("PV2"), PFC("SPC"), SPC("SPC"), CPL("CPL"), SGT("SGT"), SSG("SSG"),
    SFC("SFC"), MSG("MSG"), SM("SM"), CSM("CSM"), SMA("SMA"),
    LT("LT"), CPT("CPT"), MAJ("MAJ"), LTC("LTC"), COL("COL"),
    BG("BG"), MG("MG"), LTG("LTG"), GEN("GEN"), GOA("GOA"),
    WO1("WO1"), CW2("CW2"), CW3("CW3"), CW4("CW4"), CW5("CW5")
    ;
    
    private final String mString;
    
    private Rank(String text) {
        mString = text;
    }
    
    @Override
    public String toString() {
        return mString;
    }
}
