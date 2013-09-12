package com.hotan.ninetripleone.supply.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Class that manages the general loading of XLS and XLSX files.
 * <br> This loads files from data subdirectory from the res directory.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class POILoader {

    private POILoader() {}
    
    /**
     * 
     * 
     * @param name
     * @return 
     * @throws IOException
     */
    public static HSSFWorkbook getXLSWorkbook(String name) throws IOException {
        // Validate the name.
        if (name == null)
            throw new IllegalArgumentException("Illegal name of XLS to load: " + name);

        // Creates an input stream for this 
        File f = new File("res/data/" + name); 
        if (!f.exists()) 
            throw new FileNotFoundException("Unable to find: " + "res/data/" + name);

        // Attempt to construct the workbook via the input stream     
        return new HSSFWorkbook(new FileInputStream(f));
    }
    
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
        if (name == null)
            throw new IllegalArgumentException("Illegal name of XLSX to load: " + name);

        // Creates an input stream for this 
        File f = new File("res/data/" + name); 
        if (!f.exists()) 
            throw new FileNotFoundException("Unable to find: " + "res/data/" + name);

        // Attempt to construct the workbook via the input stream     
        return new XSSFWorkbook(new FileInputStream(f));
    }
    
}
