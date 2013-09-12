package com.hotan.ninetripleone.supply.forms;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hotan.ninetripleone.supply.model.EndItem;
import com.hotan.ninetripleone.supply.model.EndItemGroup;
import com.hotan.ninetripleone.supply.model.MOS;
import com.hotan.ninetripleone.supply.util.POITemplateLoader;

public class EndItemCoverPage {

    private static final Logger LOG = Logger.getLogger(EndItemCoverPage.class.getSimpleName());

    // NOTE: The cover page currently only supports maximum
    // of 24 items per page.
    
    /**
     * Reference to internal workbook
     */
    private final HSSFSheet mSheet;

    /**
     * 
     */
    private final HSSFCell mMinSNCell, mMaxSNCell;

    /**
     * Current cell to place
     */
    private HSSFCell mCurrentCell;

    /**
     * Loads a template for the cover page to store as XLS
     * defined by outputFile.
     * 
     * @param list of items to present on the cover page
     * @throws IOException 
     */
    public EndItemCoverPage() throws IOException {

        HSSFWorkbook workbook = POITemplateLoader.getXLSCoverPage();
        // Clone the template.
        HSSFSheet templateCpy = workbook.cloneSheet(0);
        mSheet = templateCpy;
        
        // Minimum cell
        HSSFRow row = mSheet.getRow(3); // Get the 3rd row
        HSSFCell minCell = row.getCell(1); // get the second column of the third row
        mMinSNCell = minCell;
       
        // Maximum cell
        row = mSheet.getRow(6);
        HSSFCell maxCell = row.getCell(6);
        mMaxSNCell = maxCell;
        
        mCurrentCell = mMinSNCell;
        checkRep();
    }

    /**
     * 
     * 
     * @param item
     */
    public void addEndItem(EndItemGroup items) {
        setName(items.getName());
        setLIN(items.getLIN());
        List<EndItem> itemLst = items.getItems();
        setQuantity(itemLst.size());
        setNSN(items.getNSN());
    }

    public void setName(String name) {
        if (name == null) return;
        // Get the cell for the name of the endpoint.
        HSSFRow row = mSheet.getRow(0);
        HSSFCell cell = row.getCell(1);
        cell.setCellValue(name);
    }

    public void setQuantity(int qty) {
        HSSFRow row = mSheet.getRow(0);
        HSSFCell cell = row.getCell(6);
        cell.setCellValue(qty);
    }
    
    public void setLIN(String lin) {
        if (lin == null) return;
        
        HSSFRow row = mSheet.getRow(1);
        HSSFCell cell = row.getCell(1);
        cell.setCellValue(lin);
    }

    public void setNSN(String nsn) {
        if (nsn == null) return;
        
        HSSFRow row = mSheet.getRow(2);
        HSSFCell cell = row.getCell(1);
        cell.setCellValue(nsn);
    }
    
    public void setMOS(MOS mos) {
        if (mos == null) return;
        setMos(mos.toString());
    } 
    
    private void setMos(String mos) {
        if (mos == null) return;
        
        HSSFRow row = mSheet.getRow(1);
        HSSFCell cell = row.getCell(5);
        cell.setCellValue(mos.toString());
    }
    
    public void setLocation(String location) {
        if (location == null) return;
        
        HSSFRow row = mSheet.getRow(2);
        HSSFCell cell = row.getCell(5);
        cell.setCellValue(location);
    }

    public void addSerialNumber(String serialNumber) {
        if (serialNumber == null) return;
        
        mCurrentCell.setCellValue(serialNumber);
        incrementSerialCell();
    }

    /**
     * Returns the current sheet with the data presented
     */
    public HSSFSheet getSheet() {
        return mSheet;
    }

    private void incrementSerialCell() {
        if (mCurrentCell.equals(mMaxSNCell)) {
            // TODO Implement a way to expand the space for more rows to be added.
            LOG.warning("No more cells available to write data");
            return;
        }
        int row = mCurrentCell.getRowIndex();
        int col  = mCurrentCell.getColumnIndex();
        if (col == mMaxSNCell.getColumnIndex()) { // Reached the end of one row
            col = mMinSNCell.getColumnIndex();
            row += 1;
        } else { // We are still on the current row but 
            // different cell within that row
            col += 1;
        }
        mCurrentCell = mSheet.getRow(row).getCell(col);
    }

    private void checkRep() {
        assert mSheet != null: "Workbook not found";
    }

}
