package com.hotan.ninetripleone.supply.forms;

/**
 * Base class for deficiencies of a particular end item.  Therefore every individual EndItem
 * will have a single 2404.
 *  
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public abstract class DeficiencyEntryBase {

    /**
     * Status of the deficiency.
     * 
     * @author Michael Hotan, michael.hotan@gmail.com
     */
    enum STATUS {
        OK(""), INOP("X"), REQUIRES_INSPECTION("-"), MATERIAL_DEFECT("/");
    
        private final String mString;
        
        /**
         * Creates a Status enumerated type for deficiency.
         * 
         * @param canonical Canonical form the of the status. 
         */
        private STATUS(String canonical) {
            mString =canonical;
        }
        
        @Override
        public String toString() {
            return mString;
        }
    }
    
    /**
     * Necessary blocks for deficiency entry for a 2404.
     */
    private final String tmItemNum, deficiencies, correctiveAction;
    
    /**
     * The status block for the 2404 entry.
     */
    private final STATUS status;
    
    /**
     * Creates a deficiency for an end item part.
     * 
     * @param tmNum TM Number of EndItem part.
     * @param defs Written text of all the deficiency.
     * @param corActions Corrective action.
     * @param status Status of the deficiency.
     */
    public DeficiencyEntryBase(String tmNum, String defs, String corActions, STATUS status) {
        if (tmNum == null) tmNum = "";
        if (defs == null) defs = "";
        if (corActions == null) corActions = "";
        if (status == null) throw new NullPointerException("Cant have null status");
        
        this.tmItemNum = tmNum;
        this.deficiencies = defs;
        this.correctiveAction = corActions;
        this.status = status;
    }
    
    /**
     * @return Return the status of the Deficiency.
     */
    public STATUS getStatus() {
        return status;
    }
    
    /**
     * @return deficiency text of the end item part.
     */
    public String getDeficiency() {
        return deficiencies;
    }
    
    /**
     * @return Corrective action.
     */
    public String getCorrectActions() {
        return correctiveAction;
    }
    
    /**
     * @return TM Number of the enditem part.
     */
    public String getTmNum() {
        return tmItemNum;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass().equals(getClass())) return false;
        DeficiencyEntryBase base = (DeficiencyEntryBase) o;
        return base.tmItemNum.equals(tmItemNum) && base.deficiencies.equals(deficiencies) 
                && base.correctiveAction.equals(correctiveAction) && base.status == status;
    }
    
    @Override
    public int hashCode() {
        return tmItemNum.hashCode() + 3 * deficiencies.hashCode() + 7 * correctiveAction.hashCode() + 11 * status.hashCode();
    }
    
    @Override
    public String toString() {
        return status + " TM Item Num: " + tmItemNum + " Deficiency: " +  deficiencies + " Corrective Actions: " + correctiveAction;
    }
}
