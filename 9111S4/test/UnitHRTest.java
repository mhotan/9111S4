import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hotan.ninetripleone.supply.model.EndItem;
import com.hotan.ninetripleone.supply.model.EndItemGroup;
import com.hotan.ninetripleone.supply.model.Operator;
import com.hotan.ninetripleone.supply.model.Rank;
import com.hotan.ninetripleone.supply.model.UnitLevelHandReceipt;
import com.hotan.ninetripleone.supply.util.POILoader;


public class UnitHRTest {

    private static final int NUM_ITEMS = 49;
    
    private static UnitLevelHandReceipt mValidHR;
    
    @BeforeClass
    public static void setupClass() throws IOException {
        HSSFWorkbook wb = POILoader.getXLSWorkbook("9111_UNIT_HR.xls");
        mValidHR = new UnitLevelHandReceipt(wb);
    }
    
    @Test
    public void testValidDate() {
        Date datePrep = mValidHR.getDatePrepared();
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2013, 8, 9);
        assertEquals("Date equals correct value", cal.getTime(), datePrep);
    }
    
    @Test
    public void testValidUIC() {
        assertEquals("InCorrect UIC", "WTN6A0", mValidHR.getUIC());
    }

    @Test
    public void testValidDESC() {
        assertEquals("InCorrect DESC", "1ST BN, 19TH SFG, CO A", mValidHR.getDESC());
    }
    
    @Test
    public void testTeam() {
        assertEquals("InCorrect team", "911", mValidHR.getTeam());
    }
    
    @Test
    public void testFrom() {
        assertEquals("InCorrect from operator", new Operator("JONATHAN", "TSCHETTER", Rank.MAJ), mValidHR.getWhoFrom());
    }
    
    @Test
    public void testTo() {
        assertEquals("InCorrect from operator", new Operator("JAMES", "MITCHELL", Rank.CPT), mValidHR.getWhoTo());
    }
    
    @Test
    public void testCorrectNumEndItems() {
        assertEquals("Correct number of Enditems", NUM_ITEMS, mValidHR.getGroups().size());
    }
    
    @Test 
    public void testCorrectNumOfGPSs() {
        List<EndItemGroup> enditems = mValidHR.getGroups();
        EndItemGroup gpss = enditems.get(0);
        List<EndItem> items = gpss.getItems();
        assertEquals("Incorrect number of GPSs", 12, items.size());
        assertTrue("Not serialized", gpss.getSerialized());
    }
}
