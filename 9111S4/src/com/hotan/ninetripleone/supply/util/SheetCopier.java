package com.hotan.ninetripleone.supply.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 *
 */
public class SheetCopier {

    private static final Logger LOG = Logger.getLogger(SheetCopier.class.getSimpleName());
    
    private SheetCopier() {}

    /**
     * Copy sheet including all its styles
     * 
     * @param destination New Sheet to copy to.
     * @param source Sheet to copy from
     */
    public static void copySheets(HSSFSheet destination, HSSFSheet source){  
        copySheets(destination, source, true);  
    }  

    /**
     * Copies a sheet from one source to another.
     * 
     * @param destination Destination HSSFSheet to copy to
     * @param source Source HSSFSheet to copy from
     * @param copyStyle Flag to set if the desire Cell style are wished to copied over.
     */
    public static void copySheets(HSSFSheet destination, HSSFSheet source, boolean copyStyle){  
        int maxColumnNum = 0;  

        Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>() : null;  

        // For every row do a deep copy
        for (int i = source.getFirstRowNum(); i <= source.getLastRowNum(); i++) {  
            HSSFRow srcRow = source.getRow(i);  
            HSSFRow destRow = destination.createRow(i);  
            if (srcRow != null) {  
                copyRow(source, destination, srcRow, destRow, styleMap);  
                if (srcRow.getLastCellNum() > maxColumnNum) {  
                    maxColumnNum = srcRow.getLastCellNum();  
                }  
            }  
        }  
        
        // Adjust the widths of each column
        for (int i = 0; i <= maxColumnNum; i++) {  
            destination.setColumnWidth(i, source.getColumnWidth(i));
        }  
    }  

    /**
     * Copies the contents of a row to another row from source sheet to destination sheet.
     * 
     * @param srcSheet Source sheet to copy from
     * @param destSheet Destination sheet to copy to
     * @param srcRow Source row to copy from
     * @param destRow Destination row to copy too
     * @param styleMap 
     */
    public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow, Map<Integer, HSSFCellStyle> styleMap) {  
        
        // A collection of all the found merged regions for this row.
        Set<CellRangeAddress> mergedRegions = new HashSet<CellRangeAddress>();  
        
        // Update the height of the source row to the destination row.
        destRow.setHeight(srcRow.getHeight());  
        
        // Iterate through all the cells of this row.
        for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
            
            // Get the Cell to copy from
            HSSFCell srcCell = srcRow.getCell(j);  
            // Get the Cell to copy to
            HSSFCell destCell = destRow.getCell(j);  
            
            // If there is a source cell to copy
            if (srcCell != null) {
                
                // Create the destination node if it does not exist
                if (destCell == null) {  
                    destCell = destRow.createCell(j);  
                }  
                
                // Copy the source cell into the destination cell.
                copyCell(srcCell, destCell, styleMap);  
                
                // If this cell falls inside a merged region in the row.
                CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(), srcCell.getColumnIndex());  
                if (mergedRegion != null) {  
                    // Region newMergedRegion = new Region( destRow.getRowNum(), mergedRegion.getColumnFrom(),  
                    //  destRow.getRowNum() + mergedRegion.getRowTo() - mergedRegion.getRowFrom(), mergedRegion.getColumnTo() );  
                    
                    // TODO Verify if creating a copy of the data range is necessary for this
                    CellRangeAddress newMergedRegion = new CellRangeAddress(
                            mergedRegion.getFirstRow(), mergedRegion.getLastRow(), 
                            mergedRegion.getFirstColumn(), mergedRegion.getLastColumn());  

                    // If the map was not already added to the row
                    // Add it to our internal tracking set and destination sheet.
                    if (!mergedRegions.contains(newMergedRegion)) {  
                        
                        // Add to our known regions
                        mergedRegions.add(newMergedRegion);
                        
                        // Add the merged region to the sheet.
                        destSheet.addMergedRegion(newMergedRegion);  
                    }  
                }  
            }  
        }  
    }  

    /**
     * Copies the source Cell into the destination Cell.
     * <br>Copies the style and the value of the cell.
     * 
     * @param srcCell Source cell to copy from
     * @param destCell Destinatin cell to copy to
     * @param styleMap StyleMap of all known CellStyles.
     */
    public static void copyCell(HSSFCell srcCell, HSSFCell destCell, Map<Integer, HSSFCellStyle> styleMap) {  
        
        // If there is a style map use it to copy styles
        if (styleMap != null) {  
            if(srcCell.getSheet().getWorkbook() == destCell.getSheet().getWorkbook()){  
                destCell.setCellStyle(srcCell.getCellStyle());  
            } else{  
                int stHashCode = srcCell.getCellStyle().hashCode();  
                HSSFCellStyle newCellStyle = styleMap.get(stHashCode);  
                if(newCellStyle == null){  
                    newCellStyle = destCell.getSheet().getWorkbook().createCellStyle();  
                    newCellStyle.cloneStyleFrom(srcCell.getCellStyle());  
                    styleMap.put(stHashCode, newCellStyle);  
                }  
                destCell.setCellStyle(newCellStyle);  
            }  
        }  
        
        // Check the cell type
        switch(srcCell.getCellType()) {  
        case HSSFCell.CELL_TYPE_STRING:  
            destCell.setCellValue(srcCell.getStringCellValue());  
            break;  
        case HSSFCell.CELL_TYPE_NUMERIC:  
            destCell.setCellValue(srcCell.getNumericCellValue());  
            break;  
        case HSSFCell.CELL_TYPE_BLANK:  
            destCell.setCellType(HSSFCell.CELL_TYPE_BLANK);  
            break;  
        case HSSFCell.CELL_TYPE_BOOLEAN:  
            destCell.setCellValue(srcCell.getBooleanCellValue());  
            break;  
        case HSSFCell.CELL_TYPE_ERROR:  
            destCell.setCellErrorValue(srcCell.getErrorCellValue());  
            break;  
        case HSSFCell.CELL_TYPE_FORMULA:  
            destCell.setCellFormula(srcCell.getCellFormula());  
            break;  
        default:
            LOG.warning("Unsupported Cell Type " + srcCell.getCellType());
            break;  
        }  
    }  
    
    /**
     * 
     * 
     * @param sheet Sheet to check in
     * @param rowNum The row number to check if it is in a merged area
     * @param colNum The column number of the cell to check if it is in a merged area
     * @return The range Range of cells that describes the merged region, null if cell is not in merged region.
     */
    public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum, int colNum) {  
        // Iterate through all the merged regions on the sheet.
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {  
            
            // Get the merged range the specified index.
            CellRangeAddress merged = sheet.getMergedRegion(i);  
            // If the row and column specified falls withing the merged area return the area
            if (merged.isInRange(rowNum, colNum)) {  
                return merged;  
            }  
        }  
        // Does not fall into merged region
        return null;  
    }  
}
