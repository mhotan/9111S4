package com.hotan.ninetripleone.supply.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class POITemplateLoader {

    private POITemplateLoader() {}

    /**
     * Class that has the ability to load xlsx file
     * <br> name must be suffixed with xlsx
     * 
     * @param name
     * @return 
     * @throws FileNotFoundException 
     */
    public static XSSFWorkbook getXLSXWorkbook(String name) throws IOException {
        // Validate the name.
        if (name == null || !name.endsWith(".xlsx"))
            throw new IllegalArgumentException("Illegal name of template to load: " + name);

        // Creates an input stream for this 
        File f = new File("res/templates/" + name); 
        if (!f.exists()) 
            throw new FileNotFoundException("Unable to find: " + "res/templates/" + name);

        // Attempt to construct the workbook via the input stream     
        return new XSSFWorkbook(new FileInputStream(f));
    }

    private static XSSFWorkbook getKnownXLSXWorkbook(String name) {
        try {
            return getXLSXWorkbook(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static HSSFWorkbook getXLSWorkbook(String name) throws IOException {
        // Validate the name.
        if (name == null || !name.endsWith(".xls"))
            throw new IllegalArgumentException("Illegal name of template to load: " + name);

        // Creates an input stream for this 
        File f = new File("res/templates/" + name); 
        if (!f.exists()) 
            throw new FileNotFoundException("Unable to find: " + "res/templates/" + name);

        // Attempt to construct the workbook via the input stream     
        return new HSSFWorkbook(new FileInputStream(f));
    }

    private static HSSFWorkbook getKnownXLSWorkbook(String name) {
        try {
            return getXLSWorkbook(name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static HSSFWorkbook getXLSCoverPage() {
        return getKnownXLSWorkbook("end_item_coverpage.xls");
    }

}
