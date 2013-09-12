package com.hotan.ninetripleone.supply.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POIUtil;
import com.hotan.ninetripleone.supply.util.POIUtil.IndexPair;

/**
 * A Document composing of all the Unit level Sub Component hand receipt for
 * every end item we have.  broken down by CL number.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class ComponentHandReceipt {

    private static final Logger LOG = Logger.getLogger(ComponentHandReceipt.class.getSimpleName());
    
    
    private final HSSFWorkbook mWorkbook;
    
    private final ObservableList<EndItemGroup> mGroups;
    
    private final Map<String, HSSFSheet> mMap;
    
    private final Operator mFromIndiv, mToIndiv;
    
    /**
     * Creates a Component Hand Receipt from an Excel Workbook.
     * 
     * @param wb Workbook to use.
     * @throws FormatException Could not interpret the XLS file as Component Hand receipt.
     */
    public ComponentHandReceipt(HSSFWorkbook wb) throws FormatException {
        if (wb == null) 
            throw new NullPointerException(getClass().getSimpleName() + "() Workbook cannot be null");
        mWorkbook = wb;
        mGroups = FXCollections.observableArrayList();
        mMap = new HashMap<String, HSSFSheet>();
        
        HSSFSheet sheetOne = wb.getSheetAt(0);
        mFromIndiv = getFrom(sheetOne);
        mToIndiv = getTo(sheetOne);
        
        // Iterate through all the sheets and build
        int size = mWorkbook.getNumberOfSheets();
        for (int i = 0; i < size; ++i) {
            HSSFSheet sheet = mWorkbook.getSheetAt(i);
            if (sheet == null) {
                LOG.warning("Found null sheet for workbook " + mWorkbook + " at index " + i);
                continue;
            }
            EndItem item = processSheet(sheet);
            addEndItem(item);
        }
    }

    /**
     * This sheet represents a single end item.
     * 
     * @param sheet Sheet to develop.
     * @throws FormatException 
     */
    private EndItem processSheet(HSSFSheet sheet) throws FormatException {
        // TODO Iterate through all the sheet and extract all the information for the end item.
        String uic = getUIC(sheet);
        String desc = getDESC(sheet);
        String nsn = getNSN(sheet);
        String lin = getLIN(sheet);
        String serialNum = getSerialNumber(sheet);
        String name = getName(sheet);
        String pubDate = getPublicationDate(sheet);
        String pubNum = getPublicationNumber(sheet);
        
        EndItem item = new EndItem(name, lin, nsn);
        
        return item;
    }
    
    private void addEndItem(EndItem item) {
        if (item == null) return;
        
        // Check if there isn't already the group within this component hand receipt
        EndItemGroup group = getGroup(item.getNSN(), item.getLin());
        if (group == null) {
            group = new EndItemGroup(item.getName(), item.getLin(), item.getNSN(), item.getHasSN());
            add(group);
        } 
        group.add(item);
    }
    
    private static final IndexPair UICDESC_LOCATION = new IndexPair(2,0);
    private static final IndexPair FROM_LOCATION = new IndexPair(3,0);
    private static final IndexPair TO_LOCATION = new IndexPair(3,6);
    private static final IndexPair NSN_LOCATION = new IndexPair(5, 0);
    private static final IndexPair LIN_LOCATION = new IndexPair(6, 0);
    private static final IndexPair SN_LOCATION = new IndexPair(7, 0);
    private static final IndexPair NAME_LOCATION = new IndexPair(5, 2);
    private static final IndexPair PUBNUM_LOCATION = new IndexPair(6, 2);
    private static final IndexPair PUBDATE_LOCATION = new IndexPair(6, 6);
    
    /**
     * Extract the UIC from the sheet
     * 
     * @param sheet
     * @return
     * @throws FormatException 
     */
    private static String getUIC(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, UICDESC_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() + " Unable to find cell for UIC");
        }
        String val = cell.getStringCellValue().trim();
        val = val.replace("UIC/DESC: ", "");
        val = val.split("/")[0];
        return val;
    }
    
    private static String getDESC(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, UICDESC_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() + " Unable to find cell for DESC");
        }
        String val = cell.getStringCellValue().trim();
        val = val.replace("UIC/DESC: ", "");
        val = val.split("/")[1];
        return val;
    }

    private static Operator getFrom(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, FROM_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is signed from");
        }
        String val = cell.getStringCellValue().trim();
        String[] details = val.replace("FROM: ", "").split("/");
        String rank = details[1];
        String[] fullName = details[0].split(", ");
        String firstName = fullName[1];
        String lastName = fullName[0];
        return new Operator(firstName, lastName, Rank.valueOf(rank));
    }
    
    private static Operator getTo(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, TO_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is to");
        }
        String val = cell.getStringCellValue().trim();
        String[] details = val.replace("TO: ", "").split("/");
        String rank = details[1];
        String[] fullName = details[0].split(", ");
        String firstName = fullName[1];
        String lastName = fullName[0];
        return new Operator(firstName, lastName, Rank.valueOf(rank));
    }
    
    private static String getNSN(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, NSN_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell represents the NSN");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("END ITEM NSN:", "").replace(" ", "");
    }

    private static String getLIN(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, LIN_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that represents the LIN");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("LIN:", "").replace(" ", "");
    }
    
    private static String getSerialNumber(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, SN_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is to");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("SERIAL NO:", "").replace(" ", "");
    }
    
    private static String getName(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, NAME_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is to");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("ITEM DESC:", "").replace(" ", "");
    }
    
    private static String getPublicationNumber(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, PUBNUM_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is to");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("PUB NUM:", "").replace(" ", "");
    }
    
    private static String getPublicationDate(HSSFSheet sheet) throws FormatException {
        HSSFCell cell = POIUtil.getCell(sheet, PUBDATE_LOCATION);
        if (cell == null) {
            throw new FormatException(ComponentHandReceipt.class.getSimpleName() 
                    + " Unable to find cell that describes who subcomponent is to");
        }
        String val = cell.getStringCellValue().trim();
        return val.replace("PUB DATE:", "").replace(" ", "");
    }
    
    
    public void add(EndItemGroup group) {
        if (group == null) return;
        if (hasEndItemGroup(group.getNSN(), group.getLIN())) return;
        mGroups.add(group);
    }
    
    
    
    /**
     * Checks if there is an EndItem Group with matcheing nsn and lin.
     * 
     * @param nsn NSN of the EndItem
     * @param lin Lin number of the EndItem
     * @return Whether or not there exist an EndItem group with the same nsn and lin
     */
    public boolean hasEndItemGroup(String nsn, String lin) {
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
    
}
