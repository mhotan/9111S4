package com.hotan.ninetripleone.supply.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Loads XLS and XLSX documents.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class JXLTemplateLoader {

    private static final Logger LOG =  Logger.getLogger(JXLTemplateLoader.class.getSimpleName());
    
    /**
     * Cannot instantiate
     */
    private JXLTemplateLoader() {}
    
    /**
     * Loads a XLS formated spreadsheet
     * <br>File must be contained in res/templates
     * 
     * @param fileName Name of the file to load
     * @return XLS work book on success, null on failure 
     * @throws FileNotFoundException file with name does not exist at res/templates/ 
     */
    public static Workbook getJXLWorkbook(String fileName) throws FileNotFoundException {
        // Validate the name.
        if (fileName == null || !fileName.endsWith(".xls"))
            throw new IllegalArgumentException("Illegal name of template to load: " + fileName);
        
        // Creates an input stream for this 
        File file = new File("res/templates/" + fileName);
        if (!file.exists()) 
            throw new FileNotFoundException("Unable to find: " + file.getAbsolutePath());
        
        try {
            return Workbook.getWorkbook(file);
        } catch (IOException e) {
            LOG.warning("Unable to load WorkBook " + e);
        } catch (BiffException e) {
            LOG.warning("Unable to load WorkBook " + e);
        } 
        return null;
    }
    
    private static Workbook getKnownJXLTemplate(String template) {
        try {
            Workbook w = getJXLWorkbook(template);
            if (w == null) {
                throw new RuntimeException("Unable to load " + template);
            }
            return w;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to load " + template);
        }
    }
    
    /**
     * Returns the workbook template page associated to CoverPage.
     * 
     * @return Workbook that is associated to CoverPage
     */
    public static Workbook getCoverPage() {
        return getKnownJXLTemplate("end_item_coverpage.xls");
    }
    
}
