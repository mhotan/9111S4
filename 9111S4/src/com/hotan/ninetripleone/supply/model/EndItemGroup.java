package com.hotan.ninetripleone.supply.model;

import java.util.Comparator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.hotan.ninetripleone.supply.model.EndItem;

/**
 * Class that comprises a group of EndItems
 */
public class EndItemGroup {

    /**
     * Inner collection of EndItems
     */
    private final ObservableList<EndItem> mItems;

    /**
     * Returns whether this item includes serial numbers
     */
    private final BooleanProperty serialized;

    /**
     * String Properties for this specific group.
     */
    private final StringProperty name, lIN, nSN;
    
    private IntegerProperty qty;

    /**
     * Constructs EndItemGroup based off predefined properties
     * 
     * @param nameProp Name property to use
     * @param LINProp LIN property to use
     * @param NSNProp NSN property to use
     * @param serialized Whether this end item requires serial number
     */
    public EndItemGroup(StringProperty nameProp, StringProperty LINProp, StringProperty NSNProp, boolean serialized) {
        this(nameProp.get(), LINProp.get(), NSNProp.get(), serialized);
        name.bind(nameProp);
        lIN.bind(LINProp);
        nSN.bind(NSNProp);
    }
    
    /**
     * Constructs EndItemGroup based off predefined properties and 
     * use a defaulted non serialized flag
     * 
     * @param nameProp Name property to use
     * @param LINProp LIN property to use
     * @param NSNProp NSN property to use
     */
    public EndItemGroup(StringProperty nameProp, StringProperty LINProp, StringProperty NSNProp) {
       this(nameProp, LINProp, NSNProp, false);
    }
    
    /**
     * EndItem based of primitive and String objects. 
     * 
     * @param name Name to use
     * @param LIN LIN to use
     * @param NSN NSN to use
     */
    public EndItemGroup(String name, String LIN, String NSN) {
        this(name, LIN, NSN, false);
    }

    public EndItemGroup(String name, String LIN, String NSN, boolean serialized) {
        this.name = new ReadOnlyStringWrapper(name);
        lIN = new ReadOnlyStringWrapper(LIN);
        nSN = new ReadOnlyStringWrapper(NSN);
        this.serialized = new SimpleBooleanProperty(serialized);
        mItems = FXCollections.observableArrayList();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    /////// Getters
    ///////////////////////////////////////////////////////////////////////////////////

    public boolean getSerialized() {
        return serialized.get();
    }
    
    public String getName() {
        return name.get();
    }
    
    public String getLIN() {
        return lIN.get();
    }
    
    public String getNSN() {
        return nSN.get();
    }
    
    public ObservableList<EndItem> getItems() {
        return mItems;
    }
    
    public int getQty() {
        return qtyProperty().get();
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /////// Setters
    ///////////////////////////////////////////////////////////////////////////////////
    
    public void add(EndItem item) {
        if (item == null || mItems.contains(item)) {
            return;
        }
        if (!(item.getName().equals(getName()) 
                && item.getLin().equals(getLIN()) 
                && item.getNSN().equals(getNSN()))) {
            throw new IllegalArgumentException("Illegal EndItem added " + item);
        }
        
        mItems.add(item);
    }
    
    public void sortBy(ORDER_BY order) {
        if (order == null) return;
        FXCollections.sort(mItems, order.getComparator());
    }
    
    public void setQty(int qty) {
        qtyProperty().set(qty);
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /////// Properties
    ///////////////////////////////////////////////////////////////////////////////////

    public IntegerProperty qtyProperty() {
        if (qty == null)
            qty = new SimpleIntegerProperty(0);
        return qty;
    }
    
    public ReadOnlyBooleanProperty serializedProperty() {
        return this.serialized;
    }
    
    public ReadOnlyStringProperty nameProperty() {
        return this.name;
    }
    
    public ReadOnlyStringProperty lINProperty() {
        return this.lIN;
    }
    
    public ReadOnlyStringProperty nSNProperty() {
        return this.nSN;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /////// Private helpers
    ///////////////////////////////////////////////////////////////////////////////////
    
    private static final Comparator<EndItem> NAME_COMPARATOR = new Comparator<EndItem>() {

        @Override
        public int compare(EndItem o1, EndItem o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private static final Comparator<EndItem> LIN_COMPARATOR = new Comparator<EndItem>() {

        @Override
        public int compare(EndItem o1, EndItem o2) {
            return o1.getLin().compareTo(o2.getLin());
        }
    };

    private static final Comparator<EndItem> NSN_COMPARATOR = new Comparator<EndItem>() {

        @Override
        public int compare(EndItem o1, EndItem o2) {
            return o1.getNSN().compareTo(o2.getNSN());
        }
    };

    public enum ORDER_BY {
        NAME("Name", NAME_COMPARATOR), 
        LIN("LIN", LIN_COMPARATOR), 
        NSN("NSN", NSN_COMPARATOR)
        ;

        private final String mName;
        private final Comparator<EndItem> mComparator;

        private ORDER_BY(String name, Comparator<EndItem> comparator) {
            mName = name;
            mComparator = comparator;
        }

        public Comparator<EndItem> getComparator() {
            return mComparator;
        }

        @Override
        public String toString() {
            return mName;
        }
    } 

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        EndItemGroup item = (EndItemGroup) o;
        return nameProperty().isEqualTo(item.nameProperty()).get() 
                && lINProperty().isEqualTo(item.lINProperty()).get()
                && nSNProperty().isEqualTo(item.nSNProperty()).get();
    }
    
    @Override
    public int hashCode() {
        // Use the properties to generate hascode
        return nameProperty().hashCode() + 3 * lINProperty().hashCode() 
                + 7 * nSNProperty().hashCode();
    }
    
    @Override
    public String toString() {
        String name = "EndItemGroup " + getName() + " NSN:" + getNSN() + " LIN:" + getLIN();
        return name;
    }

}
