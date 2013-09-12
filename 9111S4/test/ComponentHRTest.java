import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hotan.ninetripleone.supply.model.ComponentHandReceipt;
import com.hotan.ninetripleone.supply.model.EndItemGroup;
import com.hotan.ninetripleone.supply.model.UnitLevelHandReceipt;
import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POILoader;


public class ComponentHRTest {

    // We don't have sub component list for every thing.
    private static final int NUM_ITEMS = 29;
    
    private static ComponentHandReceipt HR;
    private static UnitLevelHandReceipt UnitHR;
    
    @BeforeClass
    public static void setupClass() throws IOException, FormatException {
        HSSFWorkbook wb = POILoader.getXLSWorkbook("ComponentHandReceipt.xls");
        HR = new ComponentHandReceipt(wb);
        
        HSSFWorkbook wb2 = POILoader.getXLSWorkbook("9111_UNIT_HR.xls");
        UnitHR = new UnitLevelHandReceipt(wb2);
    }
    
    @Test
    public void testNumEndItemGroups() {
        List<EndItemGroup> groups = HR.getGroups();
        assertEquals("Size not correct", NUM_ITEMS, groups.size());
    }

    @Test
    public void testAllComponentInUnit() {
        List<EndItemGroup> compGroups = HR.getGroups();
        for (EndItemGroup group: compGroups) {
            String nsn = group.getNSN();
            String lin = group.getLIN();
            assertTrue("Unit HR did not have Group " + group, UnitHR.hasGroup(nsn, lin));
        }
    }
    
    @Test
    public void testAllComponentInUnitCount() {
        List<EndItemGroup> compGroups = HR.getGroups();
        for (EndItemGroup group: compGroups) {
            String nsn = group.getNSN();
            String lin = group.getLIN();
            
            EndItemGroup unitGroup = UnitHR.getGroup(nsn, lin);
            assertNotNull("Unit HR did not have Group " + group, unitGroup);
            
            assertEquals("Not equal number of end items for " + unitGroup, unitGroup.getItems().size(), group.getItems().size());
        }
    }
    
}
