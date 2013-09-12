package com.hotan.ninetripleone.supply.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class UnitLevelHandReceipt {

    private static final Logger LOG = Logger.getLogger(UnitLevelHandReceipt.class.getSimpleName());

    private final HSSFWorkbook mWorkbook;

    private final Date mDate;
    
    private final String UIC, DESC, mTeam;
    
    private final Operator mFromIndiv, mToIndiv;

    private final ObservableList<EndItemGroup> mGroups;

    /**
     * Creates a Unit level hand receipt from a XLS workbook.
     * 
     * @param wb Workbook to use to build this UnitLevelHandReceipt
     */
    public UnitLevelHandReceipt(HSSFWorkbook wb) {
        if (wb == null)
            throw new NullPointerException("UnitLevelHandReceipt(), Null Workbook");
        mWorkbook = wb;
        
        mGroups = FXCollections.observableArrayList();

        HSSFSheet sheet = mWorkbook.getSheetAt(0);
        int firstRow = 7;
        int lastRow = sheet.getLastRowNum();
        for (int i = firstRow; i <= lastRow; ++i) {

            // For each row check if it is the header for an enditem
            HSSFRow row = sheet.getRow(i);
            if (row == null) continue;
            // If the head is not white then consider it a header.
            if (isEndItemHeader(row)) {
                processRow(row);
            }    
        }
        
        // Find the Date.
        mDate = findDate(mWorkbook);
        UIC = findUIC(mWorkbook);
        DESC = findDESC(mWorkbook);
        mTeam = findTeam(mWorkbook);
        mFromIndiv = findFrom(mWorkbook);
        mToIndiv = findTo(mWorkbook);
    }

    public Operator getWhoFrom() {
        return mFromIndiv;
    }
    
    public Operator getWhoTo() {
        return mToIndiv;
    }

    public String getUIC() {
        return UIC;
    }
    
    public String getDESC() {
        return DESC;
    }
    
    public String getTeam() {
        return mTeam;
    }
    
    public Date getDatePrepared() {
        return mDate;
    }
    
    /**
     * Returns the EndItems found on this hand receipt.
     * 
     * @return Unmodifiable list of groups in the enditem
     */
    public ObservableList<EndItemGroup> getGroups() {
        return FXCollections.unmodifiableObservableList(mGroups);
    }
    
    public void addGroup(EndItemGroup group) {
        if (group == null) return;
        
    }
    
    /**
     * Checks if there is an EndItem Group with matcheing nsn and lin.
     * 
     * @param nsn NSN of the EndItem
     * @param lin Lin number of the EndItem
     * @return Whether or not there exist an EndItem group with the same nsn and lin
     */
    public boolean hasGroup(String nsn, String lin) {
        return getGroup(nsn, lin) != null;
    }
    
    /**
     * Returns EndItem group that has the same nsn and lin number. 
     * 
     * @param nsn NSN to find.
     * @param lin LIN to find.
     * @return EndItemGroup that represents the the nsn and lin inputted, or null if non are found.
     */
    public EndItemGroup getGroup(String nsn, String lin) {
        if (nsn == null || lin == null) return null;
        for (EndItemGroup group: mGroups) {
            if (group.getNSN().equals(nsn) && group.getLIN().equals(lin)) {
                return group;
            }
        }
        return null;
    }
    
    /**
     * Process the end item for the row.
     * <br>Extracts the META Data for the end item
     * <br>If serial numbers exists, collect them all for the group.
     * 
     * @param header Header of the end item.
     */
    private void processRow(HSSFRow header) {
        String lin = header.getCell(0).getStringCellValue();
        String nsn = header.getCell(2).getStringCellValue();
        String nomenclature = header.getCell(4).getStringCellValue();
        String qtyStr = header.getCell(9).getStringCellValue();
        int qty = Integer.valueOf(qtyStr);

        // Create a new EndItem group.
        EndItemGroup group;

        // Go to the next row
        HSSFRow nextRow = header.getSheet().getRow(header.getRowNum() + 1);
        
        // If the item is serialized then there will be a serial number
        // below the header
        if (nextRow == null || nextRow.getCell(0) == null 
                || nextRow.getCell(0).getStringCellValue() == null ||
                        nextRow.getCell(0).getStringCellValue().isEmpty()) {
            // Create 
            group = new EndItemGroup(nomenclature, lin, nsn, false);
            for (int i = 0; i < qty; ++i) {
                group.add(new EndItem(nomenclature, lin, nsn));
            }
            
        } else { // There are serial numbers
            
            // Create a group that are serialized.
            group = new EndItemGroup(nomenclature, lin, nsn, true);
            List<String> serials = new ArrayList<String>();
            
            // While the next row is not the header to the next end item,
            // or the number of serial numbers matches the qty specified in the form.
            while (serials.size() < qty && !isEndItemHeader(nextRow)) {
                List<String> tmp = getSerialNumbers(nextRow);
                serials.addAll(tmp);
                nextRow = header.getSheet().getRow(nextRow.getRowNum() + 1);
            }
            
            // Check the quantity matches the number of serial numbers
            if (qty != serials.size()) {
                LOG.warning("Number of serial numbers found " 
            + serials.size() + " did not match defined quantity " + qty);
            }
            
            // Add the serial numbered EndItem
            for (String serial: serials) {
                EndItem tmp = new EndItem(group.getName(), group.getLIN(), group.getNSN());
                tmp.setSn(serial);
                group.add(tmp);
            }
        }
        
        // Check if the hand receipt had an error and printed the same EndItemGroup
        // on two seperate header lines.
        int currGroupInd = mGroups.indexOf(group);
        if (currGroupInd != -1) {
            EndItemGroup curGroup = mGroups.get(currGroupInd);
            curGroup.combine(group);
        } else {
            // Update the list of groups.
            mGroups.add(group);
        }
    }

    private static final int SERIAL_NUMBER_ROW_POSITION_1 = 0;
    private static final int SERIAL_NUMBER_ROW_POSITION_2 = 4;
    private static final int SERIAL_NUMBER_ROW_POSITION_3 = 5;
    
    /**
     * Extract all the serial numbers for a given HSSF row that
     * is in the correct format.  
     * 
     * @param row Row to find all the serial numbers
     * @return List of String serial numbers
     */
    private List<String> getSerialNumbers(HSSFRow row) {
        List<String> serialNums = new ArrayList<String>();
        if (isEndItemHeader(row)) return serialNums;
        
        // Extract the three serial number position
        List<String> tmp = new ArrayList<String>();
        tmp.add(row.getCell(SERIAL_NUMBER_ROW_POSITION_1).getStringCellValue());
        tmp.add(row.getCell(SERIAL_NUMBER_ROW_POSITION_2).getStringCellValue());
        tmp.add(row.getCell(SERIAL_NUMBER_ROW_POSITION_3).getStringCellValue());
        
        // Filter through the serial numbers
        for (String tmpSerial: tmp) {
            // If the serial number is empty or null ignore.
            if (tmpSerial == null || tmpSerial.isEmpty()) continue;
            serialNums.add(tmpSerial);
        }
        return serialNums;
    }
    
    /**
     * Based on the color background of the hand receipt judge whether
     * or not the row represents an end Item header.
     * 
     * @param row Row to parse.
     * @return true if it is header false otherwise.
     */
    private static boolean isEndItemHeader(HSSFRow row) {
        // LIN Cell
        HSSFCell linCell = row.getCell(0);
        if (linCell == null) return false;
        String cellVal = linCell.getStringCellValue();
        if (cellVal == null || cellVal.length() != 6) return false;
        
        // NSN Cell
        HSSFCell nsnCell = row.getCell(2);
        if (nsnCell == null) return false;
        String nsnVal = nsnCell.getStringCellValue();
        if (nsnVal == null || nsnVal.length() != 13) return false;
        
        HSSFCell nameCell = row.getCell(4);
        if (nameCell == null) return false;
        String nameVal = nameCell.getStringCellValue();
        if (nameVal == null || nameVal.isEmpty()) return false;
        
        return true;
    }

    /**
     * Returns the Date of the workbook.
     * @param wb Workbook to find Date in
     * @return Date document was produced.
     */
    private static Date findDate(HSSFWorkbook wb) {
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow row = sheet.getRow(0);
        HSSFCell cell = row.getCell(0);

        String value = cell.getStringCellValue();
        value = value.replace("DATE PREPARED:", "");
        value = value.replace("UNIT LEVEL HAND RECEIPT", "");
        value = value.replace(" ", "");
        String[] dateStr = value.split("/");

        if (dateStr == null || dateStr.length != 3) {
            LOG.warning("Unable to parse date");
            return new Date();
        }

        Calendar cal = Calendar.getInstance();
        cal.clear();
        int month = Integer.parseInt(dateStr[0]) - 1;
        int day = Integer.parseInt(dateStr[1]);
        int year = Integer.parseInt(dateStr[2]) + 2000;
        cal.set(year, month, day);
        return cal.getTime();
    }
    
    private static Operator findTo(HSSFWorkbook wb) {
        String complete = wb.getSheetAt(0).getRow(3).getCell(5).getStringCellValue();
        String val = complete.replace("TO: ", "").trim();
        String[] details = val.split("/");
        Rank rank = Rank.valueOf(details[2]);
        String completeName = details[1];
        String[] splitName = completeName.split(", ");
        return new Operator(splitName[1], splitName[0], rank);
    }

    private static Operator findFrom(HSSFWorkbook wb) {
        String complete = wb.getSheetAt(0).getRow(2).getCell(5).getStringCellValue();
        String val = complete.replace("FROM: ", "").trim();
        String[] details = val.split("/");
        Rank rank = Rank.valueOf(details[2]);
        String completeName = details[1];
        String[] splitName = completeName.split(", ");
        return new Operator(splitName[1], splitName[0], rank);
    }

    private static String findTeam(HSSFWorkbook wb) {
        String complete = wb.getSheetAt(0).getRow(3).getCell(5).getStringCellValue();
        String val = complete.replace("TO: ", "").trim();
        return val.split("/")[0];
    }

    private static String findUIC(HSSFWorkbook wb) {
        String complete = wb.getSheetAt(0).getRow(3).getCell(0).getStringCellValue();
        String val = complete.replace("UIC/DESC: ", "").trim();
        return val.split("/")[0];
    }
    
    private static String findDESC(HSSFWorkbook wb) {
        String complete = wb.getSheetAt(0).getRow(3).getCell(0).getStringCellValue();
        String val = complete.replace("UIC/DESC: ", "").trim();
        return val.split("/")[1];
    }

}
