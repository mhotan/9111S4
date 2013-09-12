import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hotan.ninetripleone.supply.model.ComponentHandReceipt;
import com.hotan.ninetripleone.supply.model.PropertyBook;
import com.hotan.ninetripleone.supply.model.UnitLevelHandReceipt;
import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POILoader;


public class PropertyBookTest {

    private static ComponentHandReceipt HR;
    private static UnitLevelHandReceipt UnitHR;
    
    private static PropertyBook PB;
    
    @BeforeClass
    public static void setupClass() throws IOException, FormatException {
        HSSFWorkbook wb = POILoader.getXLSWorkbook("ComponentHandReceipt.xls");
        HR = new ComponentHandReceipt(wb);
        
        HSSFWorkbook wb2 = POILoader.getXLSWorkbook("9111_UNIT_HR.xls");
        UnitHR = new UnitLevelHandReceipt(wb2);
        
        PB = new PropertyBook(UnitHR, HR);
    }
    
    @Test
    public void testSize() {
        assertEquals(49, PB.size());
    }

}
