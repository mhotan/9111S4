package com.hotan.ninetripleone.supply.forms;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * A adapter that provides access to the 
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public interface InspectionFormAdapter {

    public HSSFCell getOwningUnitCell(HSSFSheet sheet);
    
    public HSSFCell getNameAndModelNumberCell(HSSFSheet sheet);
    
    public HSSFCell getSerialNumberCell(HSSFSheet sheet);
    
    public HSSFCell getMilesCell(HSSFSheet sheet);
    
    public HSSFCell getHoursCell(HSSFSheet sheet);
    
    public HSSFCell getDateCell(HSSFSheet sheet);
    
    public HSSFCell getTMNumCell(HSSFSheet sheet);
    
    public HSSFCell getTMDateCell(HSSFSheet sheet);
    
    public HSSFCell getInspectionTypeCell(HSSFSheet sheet);
 
    public HSSFCell getInspectorSignatureCell(HSSFSheet sheet);
    
    public HSSFCell getInspectorTimeCell(HSSFSheet sheet);
    
    public CellRangeAddress getDeficiencyBlock();
}
