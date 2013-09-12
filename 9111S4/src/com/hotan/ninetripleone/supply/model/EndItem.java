package com.hotan.ninetripleone.supply.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Class that represents a single end item.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class EndItem {
    
    private final StringProperty name, lin, nsn;
    private StringProperty sn, mos, location, cL, pubNum, pubDate;
    private BooleanProperty hasSN;
    
    private final ObservableList<EndItemComponent> mEndItemComps;
    private final ObservableList<EndItemBasicIssueComponent> mEndItemBII;
    
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
        
        mEndItemComps = FXCollections.observableArrayList();
        mEndItemBII = FXCollections.observableArrayList();
        
        checkRep();
    }
    

    /////////////////////////////////////////////////
    //// Setters
    /////////////////////////////////////////////////
    
    /**
     * Combines the all the values as necessary from the argument item
     * to this item.  This allows us to share the maximum amount of data possible
     * between repeating items.
     * 
     * @param item Item to combine with
     */
    public void combine(EndItem item) {
        if (this == item) return; // Can't combine with self
        if (!equals(item)) return;
        
        updateStringProp(pubDate, item.pubDate);
        updateStringProp(pubNum, item.pubNum);
        updateStringProp(mos, item.mos);
        updateStringProp(location, item.location);
        updateStringProp(cL, item.cL);
        
        if (mEndItemComps.isEmpty()) {
            mEndItemComps.addAll(item.mEndItemComps);
        }
        if (mEndItemBII.isEmpty()) {
            mEndItemBII.addAll(item.mEndItemBII);
        }
    }
    
    private static void updateStringProp(StringProperty destination, StringProperty source) {
        if (destination.isNotNull().get() && !destination.get().isEmpty()) return;
        if (source.isNull().get() || source.get().isEmpty()) return;
        destination.set(source.get());
    }
    
    public void setPubDate(String pubDate) {
        pubDateProperty().set(pubDate);
    }
    
    public void setPubNum(String pubNum) {
        pubNumProperty().set(pubNum);
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
    
    public void setSn(String serialNumber) {
        snProperty().set(serialNumber);
    }
    
    /**
     * Set the CL number whatever that is.
     * @param clNum
     */
    public void setCL(String clNum) {
        CLProperty().set(clNum);
    }
    
    
    public void addCOEI(EndItemComponent component) {
        if (component == null || mEndItemComps.contains(component)) {
            return;
        }
        mEndItemComps.add(component);
    }
    
    public void addBII(EndItemBasicIssueComponent component) {
        if (component == null || mEndItemBII.contains(component)) {
            return;
        }
        mEndItemBII.add(component);
    }
    
    public void removeCOEI(EndItemComponent component) {
        mEndItemComps.remove(component);
    }
    
    public void removeBII(EndItemBasicIssueComponent component) {
        mEndItemBII.remove(component);
    }
    
    /////////////////////////////////////////////////
    //// Getters
    /////////////////////////////////////////////////
    
    public String getPubDate() {
        return pubDateProperty().get();
    }
    
    public String getPubNum() {
        return pubNumProperty().get();
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
    
    public String getSn() {
        return sn.get();
    }
    
    public boolean getHasSN() {
        return hasSNProperty().get();
    }
    
    public String getCL() {
        return CLProperty().get();
    }
    
    public ObservableList<EndItemComponent> getCOEI() {
        return FXCollections.observableArrayList(mEndItemComps);
    }
    
    public ObservableList<EndItemBasicIssueComponent> getBII() {
        return FXCollections.observableArrayList(mEndItemBII);
    }
    
    /////////////////////////////////////////////////
    //// Properties
    /////////////////////////////////////////////////

    public StringProperty pubDateProperty() {
        if (pubDate == null) 
            pubDate = new SimpleStringProperty("");
        return pubDate;
    }
    
    public StringProperty pubNumProperty() {
        if (pubNum == null) 
            pubNum = new SimpleStringProperty("");
        return pubNum;
    }
    
    public StringProperty CLProperty() {
        if (cL == null) 
            cL = new SimpleStringProperty("");
        return cL;
    }
    
    public ReadOnlyBooleanProperty hasSNProperty() {
        if (hasSN == null) {
            hasSN = new ReadOnlyBooleanWrapper();
            hasSN.bind(snProperty().isNotNull());
        }
        return hasSN;
    }
    
    public StringProperty snProperty(){
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
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        EndItem item = (EndItem) o;
        return linProperty().isEqualTo(item.linProperty()).get()
                && nsnProperty().isEqualTo(item.nsnProperty()).get()
                && snProperty().isEqualTo(item.snProperty()).get();
    }
    
    @Override
    public int hashCode() {
        // Use the properties to generate hascode
        return 3 * linProperty().hashCode() 
                + 7 * nsnProperty().hashCode() + 11 * snProperty().hashCode();
    }
    
    @Override
    public String toString() {
        String name = "EndItem " + getName() + " NSN:" + getNSN() + " LIN:" + getLin();
        if (getHasSN()) {
            name += " SN:" + getSn(); 
        }
        return name;
    }
    
    private void checkRep() {
        assert name != null;
        assert lin != null;
        assert nsn != null;
    }
}
