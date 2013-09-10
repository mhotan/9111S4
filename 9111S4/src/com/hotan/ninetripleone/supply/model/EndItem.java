package com.hotan.ninetripleone.supply.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class that represents a single end item.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class EndItem {

    private final StringProperty name, lin, nsn;
    private StringProperty sn, mos, location;
    
    // Boolean reference that labels if this end item
    // has a serial number.
    private final BooleanProperty hasSerialNumber;

    /**
     * Constructs a bare bones end item.
     * <br> By default there is not items
     * 
     * @param name Name of the enditem as it shows on Unti levet HR
     * @param lin Lin number
     * @param nsn NSN number
     */
    public EndItem(String name, 
            String lin, 
            String nsn) {
        if (name == null) {
            throw new IllegalArgumentException("EndItem(), Null name not allowed");
        } 
        if (lin == null) {
            throw new IllegalArgumentException("EndItem(), Null lin not allowed");
        } 
        if (nsn == null) {
            throw new IllegalArgumentException("EndItem(), Null nsn not allowed");
        } 

        this.name = new ReadOnlyStringWrapper(name);
        this.lin = new ReadOnlyStringWrapper(lin);
        this.nsn = new ReadOnlyStringWrapper(nsn);
        hasSerialNumber = new ReadOnlyBooleanWrapper();
        hasSerialNumber.bind(snProperty().isNotNull());
        
        checkRep();
    }

    /////////////////////////////////////////////////
    //// Setters
    /////////////////////////////////////////////////

    /**
     * Sets the serial number for this end item.
     * 
     * @param serial Serial number to set 
     */
    public void setSn(String serial) {
        snProperty().set(serial);
    }
    
    public void setMos(MOS mos) {
        mosProperty().set(mos.toString());
    }

    public void setMos(String mos) {
        mosProperty().set(mos);
    }
    
    public void setLocation(String location) {
        locationProperty().set(location);
    }
    
    /////////////////////////////////////////////////
    //// Getters
    /////////////////////////////////////////////////
    
    public String getSn() {
        return snProperty().get();
    }
    
    public String getMos() {
        return mosProperty().get();
    }
    
    public String getLocation() {
        return locationProperty().get();
    }
    
    public String getName() {
        return name.get();
    }
    
    public String getLin() {
        return lin.get();
    }
    
    public String getNSN() {
        return nsn.get();
    }
    
    public boolean getHasSerialNumber() {
        return hasSerialNumber.get();
    }
    
    /////////////////////////////////////////////////
    //// Properties
    /////////////////////////////////////////////////

    public StringProperty snProperty() {
        if (sn == null)
            sn = new SimpleStringProperty(null);
        return sn;
    }
    
    public StringProperty mosProperty() {
        if (mos == null) 
            mos = new SimpleStringProperty(null);
        return mos;
    }
    
    public StringProperty locationProperty() {
        if (location == null)
            location = new SimpleStringProperty(null);
        return location;
    }
    
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }
    
    public ReadOnlyStringProperty linProperty() {
        return lin;
    }
    
    public ReadOnlyStringProperty nsnProperty() {
        return nsn;
    }
    
    public BooleanProperty hasSerialNumberProperty() {
        return hasSerialNumber;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        EndItem item = (EndItem) o;
        return nameProperty().isEqualTo(item.nameProperty()).get() 
                && linProperty().isEqualTo(item.linProperty()).get()
                && nsnProperty().isEqualTo(item.nsnProperty()).get()
                && snProperty().isEqualTo(item.snProperty()).get();
    }
    
    @Override
    public int hashCode() {
        // Use the properties to generate hascode
        return nameProperty().hashCode() + 3 * linProperty().hashCode() 
                + 7 * nsnProperty().hashCode() + 11 * snProperty().hashCode();
    }
    
    private void checkRep() {
        assert name != null;
        assert lin != null;
        assert nsn != null;
        assert hasSerialNumber != null;
    }
}