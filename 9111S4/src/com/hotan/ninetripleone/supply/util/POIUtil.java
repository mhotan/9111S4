package com.hotan.ninetripleone.supply.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.PrintSetup;

public class POIUtil {

    private POIUtil() {}
    
    /**
     * Fits sheet to the page.
     * 
     * @param sheet Sheet to fit.
     */
    public static void fitSheetToPage(HSSFSheet sheet) {
        if (sheet == null) return;
        PrintSetup ps = sheet.getPrintSetup();
        sheet.setAutobreaks(true);
        ps.setFitHeight((short)1);
        ps.setFitWidth((short)1);
    }
    
    public static class IndexPair {
        
        public final int row, col;
        
        public IndexPair(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
    }
    
    /**
     * Finds a Cell inside the sheet at location specified by index.
     * 
     * @param sheet Sheet to find cell in
     * @param index Index that contains row and col location
     * @return null if no cell exists, or cell at row and col
     */
    public static HSSFCell getCell(HSSFSheet sheet, IndexPair index) {
        return getCell(sheet, index.row, index.col);
    }
    
    /**
     * Return the Cell at the specified index.
     * 
     * @param rowInd 0-based indexed of the row
     * @param colInd 0-based indexed of the row
     * @return null if no cell exists, or cell at row and col
     */
    public static HSSFCell getCell(HSSFSheet sheet, int rowInd, int colInd) {
        HSSFRow row = sheet.getRow(rowInd);
        if (row == null) return null;
        return row.getCell(colInd);
    }
    
}
