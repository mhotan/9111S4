package com.hotan.ninetripleone.supply.util;

import java.util.HashSet;
import java.util.Set;

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
        
        private static Set<IndexPair> mCache;
        
        private IndexPair(int row, int col) {
            this.row = row;
            this.col = col;
        }
        
        /**
         * Returns the value of the index pair with these proceding values.
         * 
         * @param row Row of the index pair
         * @param col Column of the index pair
         * @return  Index pair with the same row and col.
         */
        public static IndexPair valueOf(int row, int col) {
            if (mCache == null) 
                mCache = new HashSet<POIUtil.IndexPair>();
            
            IndexPair newPair = new IndexPair(row, col);
            for (IndexPair pair : mCache) {
                if (pair.equals(newPair)) {
                    return pair;
                }
            }
            // Add the new value to the cache.
            mCache.add(newPair);
            return newPair;
        }
        
        /**
         * Because XLS documents work with the origin in the top left corner of the
         * document, The index on the right is the index with the proceeding 
         * column.
         * 
         * @return Index of the proceeding column but same row.
         */
        public IndexPair indexOnRight() {
            return IndexPair.valueOf(row, col + 1);
        }
        
        /**
         * Because XLS documents work with the origin in the top left corner of the
         * document, The index on the left is the index with the preceding 
         * column.
         * 
         * @return Index of the preceding column but same row.
         */
        public IndexPair indexOnLeft() {
            if (col == 0) return null;
            return IndexPair.valueOf(row, col - 1);
        }
        
        /**
         * Because XLS documents work with the origin in the top left corner of the
         * document, The index on the top is the index with the preceding 
         * row.
         * 
         * @return Index of the preceding row but same column.
         */
        public IndexPair indexOnTop() {
            if (row == 0) return null;
            return IndexPair.valueOf(row - 1, col);
        }
        
        /**
         * Because XLS documents work with the origin in the top left corner of the
         * document, The index on the top is the index with the proceeding 
         * row.
         * 
         * @return Index of the proceeding row but same column.
         */
        public IndexPair indexOnBottom() {
            return IndexPair.valueOf(row + 1, col);
        }
        
        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (!o.getClass().equals(getClass())) return false;
            IndexPair i = (IndexPair) o;
            return i.row == row && i.col == col;
        }
        
        @Override
        public int hashCode() {
            return row + 7 * col;
        }
        
        @Override
        public String toString() {
            return "(" + row + ", " + col + ")";
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
    
    /**
     * Given an HSSFCell check if there is a String value associated with it.
     * 
     * @param cell Cell to check string value for.
     * @return Whether there is a String value in this cell.
     */
    public static boolean hasStringValue(HSSFCell cell) {
        if (cell == null) return false;
        String cellVal = cell.getStringCellValue();
        if (cellVal == null) return false;
        return !cellVal.trim().isEmpty();
    }
    
}
