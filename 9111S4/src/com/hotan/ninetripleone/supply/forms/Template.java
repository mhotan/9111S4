package com.hotan.ninetripleone.supply.forms;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public abstract class Template {

    private final Workbook mWb;
    
    public Template(Workbook wb) {
        mWb = wb;
    }
    
    /**
     * Return the underlying workbook
     *  
     * @return Returns the underlying workbook.
     */
    protected Workbook getWorkbook() {
        return mWb;
    }
    
    /**
     * Saves a copy of the template to the directory path and defined name.
     * 
     * @param dirPath Directory path to save copy to
     * @param name Name to associate to copy with.
     * @throws IOException Error occured when writing out the coverpage
     */
    public void save(String dirPath, String name) throws IOException {
        if (name == null)
            throw new NullPointerException("Template.save() Can't save Null name");
        if (name.isEmpty()) 
            throw new IllegalArgumentException("Template.save() Name must not be empty");
        if (dirPath == null)
            dirPath = "";
        String fullPath = dirPath;
        if (!fullPath.endsWith("/"))
            fullPath += "/";
        
        // Make sure the name ends in xls or .xlsx
        if (!(name.endsWith(".xls") || name.endsWith(".xlsx"))) {
            name += ".xls";
            if  (mWb instanceof XSSFWorkbook) {
                name += "x";
            }
        }
        fullPath += name;
        
        Path p = Paths.get(fullPath);
        Files.deleteIfExists(p);
        p = Files.createFile(p);
        OutputStream os = Files.newOutputStream(p);
        mWb.write(os);
        os.close();
    }
    
}
