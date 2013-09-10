import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

import com.hotan.ninetripleone.supply.forms.EndItemCoverPage;
import com.hotan.ninetripleone.supply.model.MOS;


public class CoverPageTest {

    
    
    @Test
    public void test() throws IOException {
        HSSFWorkbook book = new HSSFWorkbook();
        EndItemCoverPage cp = new EndItemCoverPage();
        cp.setName("Test name");
        cp.setMos(MOS.CHARLIE);
        
    }

}
