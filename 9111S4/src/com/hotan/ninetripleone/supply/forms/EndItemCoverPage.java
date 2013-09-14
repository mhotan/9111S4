package com.hotan.ninetripleone.supply.forms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;

import com.hotan.ninetripleone.supply.model.EndItem;
import com.hotan.ninetripleone.supply.model.EndItemGroup;
import com.hotan.ninetripleone.supply.model.MOS;
import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POITemplateLoader;
import com.hotan.ninetripleone.supply.util.POIUtil;
import com.hotan.ninetripleone.supply.util.POIUtil.IndexPair;

public class EndItemCoverPage extends Template {

    private static final Logger LOG = Logger.getLogger(EndItemCoverPage.class.getSimpleName());

    private static final CoverPageAdapter DEFAULT_ADAPTER = new DefaultAdapter();
    
    // NOTE: The cover page currently only supports maximum
    // of 24 items per page.

    /**
     * Reference to internal workbook
     */
    private final HSSFSheet mSheet;

    /**
     * Cells that contain the dynamic data.
     */
    private final HSSFCell nameCell, qtyCell, linCell, nsnCell, mosCell, locCell;

    /**
     * Range of cells for serial numbers
     */
    private final CellRangeAddress snRange;

    /**
     * Adapter that correctly extracts the cell.
     */
    private final CoverPageAdapter mAdapter;
    
    /**
     * Current cell to place
     */
    private IndexPair mCurrIndex;

    /**
     * Creates a cover page from the default
     * 
     * @throws FormatException 
     */
    public EndItemCoverPage() throws FormatException {
        this(POITemplateLoader.getXLSCoverPage(), DEFAULT_ADAPTER);
    }

    /**
     * Loads a template for the cover page to store as XLS
     * defined by outputFile.
     * 
     * @param list of items to present on the cover page
     * @throws FormatException If the adapter is misformated for the template.
     */
    protected EndItemCoverPage(HSSFWorkbook template, CoverPageAdapter adapter) throws FormatException {
        super(template);
        mSheet = template.getSheetAt(0);
        mAdapter = adapter;

        // Set the range of cells
        snRange = mAdapter.getSNRangeAddress(mSheet);

        StringBuffer errBuf = new StringBuffer();
        nameCell = mAdapter.getNameCell(mSheet);
        if (nameCell == null)
            errBuf.append("Can't locate Name cell\n");
        qtyCell = mAdapter.getQuantityCell(mSheet);
        if (qtyCell == null)
            errBuf.append("Can't locate Quantity cell\n");
        linCell = mAdapter.getLINCell(mSheet);
        if (linCell == null)
            errBuf.append("Can't locate LIN cell\n");
        nsnCell = mAdapter.getNSNCell(mSheet);
        if (nsnCell == null)
            errBuf.append("Can't locate NSN cell\n");
        mosCell = mAdapter.getMOSCell(mSheet);
        if (mosCell == null)
            errBuf.append("Can't locate MOS cell\n");
        locCell = mAdapter.getLocationCell(mSheet);
        if (locCell == null)
            errBuf.append("Can't locate Location cell\n");

        // If there was an error stop everything.
        if (errBuf.length() > 0) {
            throw new FormatException(getClass().getSimpleName() + "() " + errBuf.toString()); 
        }

        mCurrIndex = IndexPair.valueOf(snRange.getFirstRow(), snRange.getFirstColumn());
        checkRep();
    }

    /**
     * Sets the group of EndItems to Display.
     * 
     * @param group Group of EndItems to present.
     */
    public void setGroup(EndItemGroup group) {
        setName(group.getName());
        setLIN(group.getLIN());
        List<EndItem> itemLst = group.getItems();
        setQuantity(itemLst.size());
        setNSN(group.getNSN());
        setMOS(group.getMos());
        
        Set<String> locations = new HashSet<String>();
        for (EndItem item : itemLst) {
            String location = item.getLocation();
            if (location == null || location.isEmpty()) continue;
            locations.add(location);
        }
        StringBuffer buf = new StringBuffer();
        List<String> locList = new ArrayList<String>(locations);
        for (int i = 0; i < locList.size(); ++i) {
            buf.append(locList.get(i));
            if (i != locList.size() - 1) {
                buf.append("; ");
            }
        }
        setLocation(buf.toString());
        
        if (!group.getSerialized())
            return;
        
        for (EndItem item : itemLst) {
            try {
                addSerialNumber(item.getSn());
            } catch (FormatException e) {
                LOG.warning("Error while adding serial number");
            }
        }
    }

    /**
     * Sets the name of this cover page
     * 
     * @param name 
     */
    public void setName(String name) {
        if (name == null) return;
        nameCell.setCellValue(name);
    }

    
    public void setQuantity(int qty) {
        qtyCell.setCellValue(qty);
    }

    public void setLIN(String lin) {
        if (lin == null) return;
        linCell.setCellValue(lin);
    }

    public void setNSN(String nsn) {
        if (nsn == null) return;
        nsnCell.setCellValue(nsn);
    }

    public void setMOS(MOS mos) {
        if (mos == null) return;
        setMOS(mos.toString());
    } 

    private void setMOS(String mos) {
        if (mos == null) return;
        mosCell.setCellValue(mos);
    }

    public void setLocation(String location) {
        if (location == null) return;
        locCell.setCellValue(location);
    }

    public void addSerialNumber(String serialNumber) throws FormatException {
        if (serialNumber == null) return;
        HSSFCell currCell = POIUtil.getCell(mSheet, mCurrIndex);
        if (currCell == null) 
            throw new FormatException("Unable to find cell at " + mCurrIndex);
        currCell.setCellValue(serialNumber);
        
        try {
            incrementSerialCell();
        } catch (FormatException e) {
            throw new FormatException("addSerialNumber() " + e.getMessage());
        }
    }

    /**
     * Sets the overall picture to the path found in the argument.
     * 
     * @param imagePath Image path of the picture
     * @throws IOException Error occurred while loading the image.
     */
    public void setOverallPic(String imagePath) throws IOException {
        HSSFCell cell = mAdapter.getOverallPicCell(mSheet);
        if (cell == null) return;
        addImageToCell(imagePath, cell);
    }
    
    /**
     * Sets the serial number picture to the path found in the argument.
     * 
     * @param imagePath Image path of the picture
     * @throws IOException Error occurred while loading the image.
     */
    public void setSNPic(String imagePath) throws IOException {
        HSSFCell cell = mAdapter.getSNPicCell(mSheet);
        if (cell == null) return;
        addImageToCell(imagePath, cell);
    }
    
    /**
     * Sets the BII picture to the path found in the argument.
     * 
     * @param imagePath Image path of the picture
     * @throws IOException Error occurred while loading the image.
     */
    public void setBIIPic(String imagePath) throws IOException {
        HSSFCell cell = mAdapter.getBIIPicCell(mSheet);
        if (cell == null) return;
        addImageToCell(imagePath, cell);
    }
    
    /**
     * Returns the current sheet with the data presented
     */
    public HSSFSheet getSheet() {
        return mSheet;
    }

    private void addImageToCell(String imagePath, HSSFCell cell) throws IOException {
        if (imagePath == null) 
            throw new NullPointerException("Null image path");
        Workbook wb = getWorkbook();
        
        int type = -1;
        String nameCpy = imagePath.toLowerCase().trim();
        if (nameCpy.endsWith(".jpg") || nameCpy.endsWith(".jpeg"))
            type = Workbook.PICTURE_TYPE_JPEG;
        if (nameCpy.endsWith(".bmp") || nameCpy.endsWith(".dib"))
            type = Workbook.PICTURE_TYPE_DIB;
        if (nameCpy.endsWith(".png"))
            type = Workbook.PICTURE_TYPE_PNG;
        if (type == -1) {
            throw new IllegalArgumentException("Unsurpported type " + imagePath);
        }
        
        // Load the image and store it into the workbook.
        InputStream is = new FileInputStream(new File(imagePath));
        byte[] bytes = IOUtils.toByteArray(is);
        int pictureIdx = wb.addPicture(bytes, type);
        is.close();
        
        // Place the image in the cell provided.
        CreationHelper helper = wb.getCreationHelper();
        
        // Create the drawing patriarch.  This is the top level container for all shapes. 
        Drawing drawing = mSheet.createDrawingPatriarch();
        
        ClientAnchor anchor = helper.createClientAnchor();
        //set top-left corner of the picture,
        //subsequent call of Picture#resize() will operate relative to it
        anchor.setCol1(cell.getColumnIndex());
        anchor.setRow1(cell.getRowIndex());
        Picture pict = drawing.createPicture(anchor, pictureIdx);

        //auto-size picture relative to its top-left corner
        pict.resize();
    }
    
    private void incrementSerialCell() throws FormatException {
        IndexPair pair = mCurrIndex.indexOnRight();
        if (!snRange.isInRange(pair.row, pair.col)) {
            // Go to the next element on the first row.
            pair = IndexPair.valueOf(pair.row + 1, snRange.getFirstColumn());
            if (!snRange.isInRange(pair.row, pair.col))
                throw new FormatException("Ran out of cells");
        }
        mCurrIndex = pair;
    }

    private void checkRep() {
        assert mSheet != null: "Workbook not found";
        assert nameCell != null;
        assert qtyCell != null; 
        assert linCell != null; 
        assert nsnCell != null; 
        assert mosCell != null; 
        assert locCell != null;
    }

    private static class DefaultAdapter implements CoverPageAdapter {

        private static final IndexPair MIN_SN_INDEX = IndexPair.valueOf(3,1);
        private static final IndexPair MAX_SN_INDEX = IndexPair.valueOf(6, 6);
        private static final IndexPair NAME_INDEX = IndexPair.valueOf(0,1);
        private static final IndexPair QTY_INDEX = IndexPair.valueOf(0,6);
        private static final IndexPair LIN_INDEX = IndexPair.valueOf(1,1);
        private static final IndexPair NSN_INDEX = IndexPair.valueOf(2,1);
        private static final IndexPair MOS_INDEX = IndexPair.valueOf(1,5);
        private static final IndexPair LOC_INDEX = IndexPair.valueOf(2,5);
        private static final IndexPair OVERALL_PIC_INDEX = IndexPair.valueOf(8, 0);
        private static final IndexPair SERIAL_PIC_INDEX = IndexPair.valueOf(8, 0);
        private static final IndexPair BII_PIC_INDEX = IndexPair.valueOf(8, 0);
        
        @Override
        public HSSFCell getNameCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, NAME_INDEX);
        }

        @Override
        public HSSFCell getQuantityCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, QTY_INDEX);
        }

        @Override
        public HSSFCell getLINCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, LIN_INDEX);
        }

        @Override
        public HSSFCell getNSNCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, NSN_INDEX);
        }

        @Override
        public HSSFCell getMOSCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, MOS_INDEX);
        }

        @Override
        public HSSFCell getLocationCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, LOC_INDEX);
        }

        @Override
        public CellRangeAddress getSNRangeAddress(HSSFSheet sheet) {
            return new CellRangeAddress(
                    MIN_SN_INDEX.row, MAX_SN_INDEX.row, MIN_SN_INDEX.col, MAX_SN_INDEX.col);
        }

        @Override
        public HSSFCell getOverallPicCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, OVERALL_PIC_INDEX);
        }

        @Override
        public HSSFCell getSNPicCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, SERIAL_PIC_INDEX);
        }

        @Override
        public HSSFCell getBIIPicCell(HSSFSheet sheet) {
            return POIUtil.getCell(sheet, BII_PIC_INDEX);
        }
        
    }
    
}
