package com.hotan.ninetripleone.supply.forms;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Returns a deficiency entry.
 *  
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class DeficiencyEntry extends DeficiencyEntryBase {
    
    private DeficiencyEntry(String tmNum, String defs, String corActions,
            STATUS status) {
        super(tmNum, defs, corActions, status);
    }

    /**
     * 
     * @param tmNum
     * @param deficiencies
     * @param corActions
     * @param statusString
     * @return 
     */
    public static DeficiencyEntry valueOf(String tmNum, String deficiencies, String corActions, String statusString) {
        if (statusString.equals(STATUS.OK.toString())) {
            return new DeficiencyEntry("", deficiencies, "", STATUS.OK);
        } else if (statusString.equals(STATUS.REQUIRES_INSPECTION)) {
            return getRequiresInspectionDeficiency(tmNum, deficiencies, corActions);
        } else if (statusString.equals(STATUS.MATERIAL_DEFECT)) {
            return getMaterialDefectDeficiency(tmNum, deficiencies, corActions);
        } else if (statusString.equals(STATUS.INOP)) {
            return getInoperableDeficiency(tmNum, deficiencies, corActions);
        }
        return null;
    }
    
    public static DeficiencyEntry getInoperableDeficiency(String tmNum, String deficiencies, String corActions) {
        return new DeficiencyEntry(tmNum, deficiencies, corActions, STATUS.INOP);
    }
    
    public static DeficiencyEntry getMaterialDefectDeficiency(String tmNum, String deficiencies, String corActions) {
        return new DeficiencyEntry(tmNum, deficiencies, corActions, STATUS.MATERIAL_DEFECT);
    }
    
    public static DeficiencyEntry getRequiresInspectionDeficiency(String tmNum, String deficiencies, String corActions) {
        return new DeficiencyEntry(tmNum, deficiencies, corActions, STATUS.REQUIRES_INSPECTION);
    }
    
    public static DeficiencyEntry getOK() {
        String date = new SimpleDateFormat().format(Calendar.getInstance().getTime());
        return new DeficiencyEntry("", date, "", STATUS.OK);
    }
    
}
