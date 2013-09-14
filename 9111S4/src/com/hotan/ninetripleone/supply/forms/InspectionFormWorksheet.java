package com.hotan.ninetripleone.supply.forms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.hotan.ninetripleone.supply.util.Alphabet;
import com.hotan.ninetripleone.supply.util.ApplicationConstants;
import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POITemplateLoader;
import com.hotan.ninetripleone.supply.util.POIUtil;
import com.hotan.ninetripleone.supply.util.POIUtil.IndexPair;

/**
 * Inspection Form Worksheet or DA 2404 to fill out based upon endpoint.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class InspectionFormWorksheet extends Template {

    private static final DefaultAdapter DEFAULT_ADAPTER = new DefaultAdapter();

    /**
     * Adapter to retrieve cells.
     */
    private final InspectionFormAdapter mAdapter;

    /**
     * Sheet that contains the Inspection Worksheet itself.
     */
    private final HSSFSheet mSheet;
    
    /**
     * Cells for entry.
     */
    private final HSSFCell owningUnitCell, nameCell, 
    snCell, milesCell, hoursCell, dateCell, 
    tmNumCell, tmDateCell, inspTypeCell, inspSigCell, inspTimeCell;

    /**
     * The cell range for all the deficiency entry.
     */
    private final CellRangeAddress defRange;
    
    /**
     * List of deficiency entries
     */
    private final ObservableList<DeficiencyEntry> mEntries;
    
    /**
     * Creates a new DA FORM 2404 from the bank template.
     * 
     * @throws FormatException Exception found while loading default template
     */
    public InspectionFormWorksheet() throws FormatException {
        this(POITemplateLoader.getXLS2404(), DEFAULT_ADAPTER);
    }

    /**
     * Builds a 2404 based of an existing 2404.
     * 
     * @param wb Workbook to use as 2404
     * @param adapter Adapter to use to interpret the template
     * @throws FormatException When the workbook does not represent a 2404.
     */
    protected InspectionFormWorksheet(HSSFWorkbook wb, 
            InspectionFormAdapter adapter) throws FormatException {
        super(wb);
        mAdapter = adapter;
        mSheet = wb.getSheetAt(0);
        if (mSheet == null) {
            throw new FormatException("Unable to find template sheet workbook");
        }
        
        // Set the range of cells
        defRange = mAdapter.getDeficiencyBlock();

        StringBuffer errBuf = new StringBuffer();
        owningUnitCell = mAdapter.getOwningUnitCell(mSheet);
        if (owningUnitCell == null)
            errBuf.append("Can't locate Owning Unit cell\n");
        nameCell = mAdapter.getNameAndModelNumberCell(mSheet);
        if (nameCell == null)
            errBuf.append("Can't locate Name cell\n");
        snCell = mAdapter.getSerialNumberCell(mSheet);
        if (snCell == null)
            errBuf.append("Can't locate Serial Number cell\n");
        milesCell = mAdapter.getMilesCell(mSheet);
        if (milesCell == null)
            errBuf.append("Can't locate Miles cell\n");
        hoursCell = mAdapter.getHoursCell(mSheet);
        if (hoursCell == null)
            errBuf.append("Can't locate Hours cell\n");
        dateCell = mAdapter.getDateCell(mSheet);
        if (dateCell == null)
            errBuf.append("Can't locate Date cell\n");
        tmNumCell = mAdapter.getTMNumCell(mSheet);
        if (tmNumCell == null)
            errBuf.append("Can't locate TM Number cell\n");
        tmDateCell = mAdapter.getTMDateCell(mSheet);
        if (tmDateCell == null)
            errBuf.append("Can't locate TM Date cell\n");
        inspTypeCell = mAdapter.getInspectionTypeCell(mSheet);
        if (inspTypeCell == null)
            errBuf.append("Can't locate Inspection Type cell\n");
        inspSigCell = mAdapter.getInspectorSignatureCell(mSheet);
        if (inspSigCell == null)
            errBuf.append("Can't locate Inspector Signature cell\n");
        inspTimeCell = mAdapter.getInspectorTimeCell(mSheet);
        if (inspTimeCell == null)
            errBuf.append("Can't locate Inspection Time cell\n");
        
        mEntries = collectEntries();
    }
    
    /**
     * Adds a deficiency entry.
     * 
     * <br> Entry cannot be null
     * @param entry Entry to add this 2404
     * @return true if entry was added, false if there is not more space.
     */
    public boolean addDeficiency(DeficiencyEntry entry) {
        if (entry == null) 
            throw new NullPointerException("addDeficiency() Entry cannot be null");
        
        // If there is no more space on the list of deficiencies.
        if (mEntries.size() >= (defRange.getLastRow() - defRange.getFirstRow() + 1))
            return false;
        
        // Adds an entry to list.
        mEntries.add(entry);
        return true;
    }
    
    @Override
    public void save(String dirPath, String name) throws IOException {
        for (int i = 0; i < mEntries.size(); ++i) {
            int row = i + defRange.getFirstRow();
            HSSFCell itemNO = POIUtil.getCell(mSheet, IndexPair.valueOf(row, Alphabet.A.index()));
            HSSFCell status = POIUtil.getCell(mSheet, IndexPair.valueOf(row, Alphabet.B.index()));
            HSSFCell deficiency = POIUtil.getCell(mSheet, IndexPair.valueOf(row, Alphabet.C.index()));
            HSSFCell correctiveAction = POIUtil.getCell(mSheet, IndexPair.valueOf(row, Alphabet.H.index()));
            
            DeficiencyEntry entry = mEntries.get(i);
            itemNO.setCellValue(entry.getTmNum());
            status.setCellValue(entry.getStatus().toString());
            deficiency.setCellValue(entry.getDeficiency());
            correctiveAction.setCellValue(entry.getCorrectActions());
        }
        super.save(dirPath, name);
    }

    /**
     * 
     * 
     * @return List of Entries for this Inspection Worksheet.
     */
    private ObservableList<DeficiencyEntry> collectEntries() {
        ObservableList<DeficiencyEntry> list = FXCollections.observableArrayList();
        
        // Iterate through the deficiency blocks
        for (int rowIndex = defRange.getFirstRow(); rowIndex <= defRange.getLastRow(); ++rowIndex) {
            
            // Extract the row for the deficiency
            HSSFRow row = mSheet.getRow(rowIndex);
            if (row == null) break;
            
            // 
            DeficiencyEntry entry = getDeficiencyEntry(row);
            if (entry == null) break;
            
            // Add the entry to the list
            list.add(entry);
        }
        
        return list;
    }
    
    /**
     * Extracts DeficiencyEntry for a given row of a 2404. 
     * 
     * @param row Row to use to provide a Deficiency Entry
     * @return DeficiencyEntry that is represented by the row.
     */
    private DeficiencyEntry getDeficiencyEntry(HSSFRow row) {
        String itemNO = POIUtil.getCell(mSheet, IndexPair.valueOf(row.getRowNum(), Alphabet.A.index())).getStringCellValue();
        String status = POIUtil.getCell(mSheet, IndexPair.valueOf(row.getRowNum(), Alphabet.B.index())).getStringCellValue();
        String deficiency = POIUtil.getCell(mSheet, IndexPair.valueOf(row.getRowNum(), Alphabet.C.index())).getStringCellValue();
        String correctiveAction = POIUtil.getCell(mSheet, IndexPair.valueOf(row.getRowNum(), Alphabet.H.index())).getStringCellValue();
        return DeficiencyEntry.valueOf(itemNO, deficiency, correctiveAction, status);
    }
    
    /*
     * Cell setters
     */

    public void setOwningUnit(String unit) {
        owningUnitCell.setCellValue(unit);
    }
    
    public void setEndItemName(String name) {
        nameCell.setCellValue(name);
    }
    
    public void setSerialNumber(String sn) {
        snCell.setCellValue(sn);
    }
    
    public void setMiles(long miles) {
        milesCell.setCellValue(miles);
    }
    
    public void setHours(long hours) {
        hoursCell.setCellValue(hours);
    }
    
    public void setDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.DATE_FORMAT);
        dateCell.setCellValue(format.format(date));
    }
    
    public void setTMNumber(String number) {
        tmNumCell.setCellValue(number);
    }
    
    public void setTMDate(String date) {
        tmDateCell.setCellValue(date);
    }
    
    public void setInspectionType(String type) {
        inspTypeCell.setCellValue(type);
    }
    
    public void setInspectionSigCell(String signature) {
        inspSigCell.setCellValue(signature);
    }
    
    
    /*
     * Cell getters 
     */
    
    public void setInspectionTime(Date time) {
        SimpleDateFormat format = new SimpleDateFormat(ApplicationConstants.TIME_FORMAT);
        inspTimeCell.setCellValue(format.format(time));
    }
    
    public String getOwningUnit() {
        return owningUnitCell.getStringCellValue();
    } 

    public String getNameOfEndItem() {
        return nameCell.getStringCellValue();
    }
    
    public String getSerialNumber() {
        return snCell.getStringCellValue();
    }
    
    public String getMilesReading() {
        return milesCell.getStringCellValue();
    }
    
    public String getHoursReading() {
        return hoursCell.getStringCellValue();
    }
    
    public String getDateReading() {
        return dateCell.getStringCellValue();
    }
    
    public String getTMNumber() {
        return tmNumCell.getStringCellValue();
    }
    
    public String getTMDate() {
        return tmDateCell.getStringCellValue();
    }
    
    public String getInspectionType() {
        return inspTypeCell.getStringCellValue();
    }
    
    public String getInspectionSignature() {
        return inspSigCell.getStringCellValue();
    }
    
    public String getInspectionTime() {
        return inspSigCell.getStringCellValue();
    }
    
    /*
     * The default adapter to get reference to all the cells.
     */
    
    /**
     * Create a default adapter for handling 2404 templates
     * 
     * @author Michael Hotan, michael.hotan@gmail.com
     */
    public static class DefaultAdapter implements InspectionFormAdapter {

        private static final IndexPair OWNING_UNIT_INDEX = IndexPair.valueOf(3, 1);
        private static final IndexPair NAME_MODEL_INDEX = IndexPair.valueOf(3, Alphabet.I.index());
        private static final IndexPair SERIAL_NUM_INDEX = IndexPair.valueOf(5, Alphabet.A.index());
        private static final IndexPair MILES_INDEX = IndexPair.valueOf(5, Alphabet.D.index());
        private static final IndexPair HOURS_INDEX = IndexPair.valueOf(5, Alphabet.F.index());
        private static final IndexPair DATE_INDEX = IndexPair.valueOf(5, Alphabet.L.index());
        private static final IndexPair TMNUM_INDEX = IndexPair.valueOf(9, Alphabet.A.index());
        private static final IndexPair TMDATE_INDEX = IndexPair.valueOf(9, Alphabet.E.index());
        private static final IndexPair INSPECTION_TYPE_INDEX = IndexPair.valueOf(5, Alphabet.N.index());
        private static final IndexPair INSPECTOR_SIGNATURE_INDEX = IndexPair.valueOf(33, Alphabet.A.index());
        private static final IndexPair INSPECTOR_TIME_INDEX = IndexPair.valueOf(33, Alphabet.F.index());


        @Override
        public HSSFCell getOwningUnitCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, OWNING_UNIT_INDEX);
        }

        @Override
        public HSSFCell getNameAndModelNumberCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, NAME_MODEL_INDEX);
        }

        @Override
        public HSSFCell getSerialNumberCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, SERIAL_NUM_INDEX);
        }

        @Override
        public HSSFCell getMilesCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, MILES_INDEX);
        }

        @Override
        public HSSFCell getHoursCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, HOURS_INDEX);
        }

        @Override
        public HSSFCell getDateCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, DATE_INDEX);
        }

        @Override
        public HSSFCell getTMNumCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, TMNUM_INDEX);
        }

        @Override
        public HSSFCell getTMDateCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, TMDATE_INDEX);
        }

        @Override
        public HSSFCell getInspectionTypeCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, INSPECTION_TYPE_INDEX);
        }

        @Override
        public HSSFCell getInspectorSignatureCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, INSPECTOR_SIGNATURE_INDEX);
        }

        @Override
        public HSSFCell getInspectorTimeCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, INSPECTOR_TIME_INDEX);
        }

        @Override
        public CellRangeAddress getDeficiencyBlock() {
            return new CellRangeAddress(39, 52, Alphabet.A.index(), Alphabet.O.index());
        }

    }

}
