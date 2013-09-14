package com.hotan.ninetripleone.supply.forms;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Interface that defines how to assign the cells for the inputed sheet.
 * This class allows variation in use different kind of cover pages XLS templates.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public interface CoverPageAdapter {

    /**
     * Attempts to get the Cell that represents the Name
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getNameCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the Quantity
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getQuantityCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the LIN number
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getLINCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the LIN number
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getNSNCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the assigned MOS
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getMOSCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the Location of this item
     * <br>Cell cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getLocationCell(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell range of all the serial numbers boxes
     * <br>Range cannot be null
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public CellRangeAddress getSNRangeAddress(HSSFSheet sheet);
    
    /**
     * Attempts to get the Cell that represents the Overall picture of this item
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getOverallPicCell(HSSFSheet sheet);

    /**
     * Attempts to get the Cell that represents the Serial Number picture of this item
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getSNPicCell(HSSFSheet sheet);

    /**
     * Attempts to get the Cell that represents the Overall BII picture of this item
     * 
     * @param sheet Sheet to get Cell from.
     * @return Cell to place the value
     */
    public HSSFCell getBIIPicCell(HSSFSheet sheet);

    
}
