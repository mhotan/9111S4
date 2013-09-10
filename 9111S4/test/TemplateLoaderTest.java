import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.hotan.ninetripleone.supply.util.JXLTemplateLoader;
import com.hotan.ninetripleone.supply.util.POITemplateLoader;


public class TemplateLoaderTest {

    @Test
    public void TestLoadJXLTemplates() throws FileNotFoundException {
        assertNotNull("DA_2062 template found", JXLTemplateLoader.getJXLWorkbook("DA_2062.xls"));
        assertNotNull("DA_2404 template found", JXLTemplateLoader.getJXLWorkbook("DA_2404.xls"));
        assertNotNull("DD_1149 template found", JXLTemplateLoader.getJXLWorkbook("DD_1149.xls"));
        assertNotNull("DD_1750 template found", JXLTemplateLoader.getJXLWorkbook("DD_1750.xls"));
        assertNotNull("End Item Cover Page template found", JXLTemplateLoader.getJXLWorkbook("end_item_coverpage.xls"));
    }

    @Test
    public void TestLoadPOITemplates() throws IOException {
        assertNotNull("DA_2062 template found", POITemplateLoader.getXLSWorkbook("DA_2062.xls"));
        assertNotNull("DA_2404 template found", POITemplateLoader.getXLSWorkbook("DA_2404.xls"));
        assertNotNull("DD_1149 template found", POITemplateLoader.getXLSWorkbook("DD_1149.xls"));
        assertNotNull("DD_1750 template found", POITemplateLoader.getXLSWorkbook("DD_1750.xls"));
        assertNotNull("End Item Cover Page template found", POITemplateLoader.getXLSWorkbook("end_item_coverpage.xls"));
    }
    
    
}
