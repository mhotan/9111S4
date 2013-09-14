import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hotan.ninetripleone.supply.forms.EndItemCoverPage;
import com.hotan.ninetripleone.supply.model.EndItem;
import com.hotan.ninetripleone.supply.model.EndItemGroup;
import com.hotan.ninetripleone.supply.model.MOS;
import com.hotan.ninetripleone.supply.util.FormatException;
import com.hotan.ninetripleone.supply.util.POIUtil;
import com.hotan.ninetripleone.supply.util.SheetCopier;


public class CoverPageTest {

    private static final String DIR = "bin/test/" + CoverPageTest.class.getSimpleName();
    
    @BeforeClass
    public static void setup() throws IOException {
        Files.createDirectories(Paths.get(DIR));
    }
    
    @Test
    public void test() throws IOException, FormatException {
        HSSFWorkbook book = new HSSFWorkbook();
        book.createSheet("test");
        HSSFSheet sheet = book.getSheet("test");
        POIUtil.fitSheetToPage(sheet);
        
        EndItemGroup item = new EndItemGroup("test", "lin", "nsn");
        EndItemCoverPage cp = new EndItemCoverPage();
        cp.setGroup(item);
        
        SheetCopier.copySheets(sheet, cp.getSheet());
        File f = new File(DIR + "/" + "test.xls");
        if (f.exists())
            f.delete();
        f.createNewFile();
        
        book.write(new FileOutputStream(f));
    }
    
    @Test
    public void testSave() throws FormatException, IOException {
        EndItemGroup group = new EndItemGroup("test", "lin", "nsn", true);
        group.setMos(MOS.BRAVO);
        EndItem item = new EndItem("test", "lin", "nsn");
        item.setSn("12345");
        EndItem item2 = new EndItem("test", "lin", "nsn");
        item2.setSn("12346");
        item.setLocation("Your mom's house");
        group.add(item);
        group.add(item2);
        
        EndItemCoverPage cp = new EndItemCoverPage();
        cp.setGroup(group);
        cp.save(DIR + "/", "test2.xls");
    }

}
