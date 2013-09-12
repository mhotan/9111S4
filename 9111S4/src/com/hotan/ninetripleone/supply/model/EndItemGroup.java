package com.hotan.ninetripleone.supply.model;

import java.util.Comparator;
import java.util.List;

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
    
    public int size() {
        return mItems.size();
    }
    
    /**
     * Combines argument group into this.
     * <br>If the LIN and NSN do not match then nothing is done.
     * <br>Otherwise all the end items will attempted to be added.
     * @param group Group to combine into
     */
    public void combine(EndItemGroup group) {
        if (this == group) return; // Can't combine with self
        if (!this.equals(group)) return;
        
        // Update the name with the name of the second group
        if (getName().isEmpty() && !group.getName().isEmpty()) 
            name.set(group.getName());
        
        List<EndItem> items = group.getItems();
        for (EndItem item: items) {
            add(item);
        }
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /////// Setters
    ///////////////////////////////////////////////////////////////////////////////////
    
    public boolean contains(EndItem item) {
        return mItems.contains(item);
    }
    
    public void add(EndItem item) {
        if (item == null) {
            return;
        }
        if (!(item.getLin().equals(getLIN()) 
                && item.getNSN().equals(getNSN()))
                && item.getHasSN() == getSerialized()) {
            throw new IllegalArgumentException("Illegal EndItem added " + item);
        }
        
        // If we are serialized then just add the element
        if (getSerialized()) {
            EndItem curItem = null;
            
            // Check if we have the serial number already
            for (EndItem i: mItems) {
                if (i.getSn().equals(item.getSn())) {
                    curItem = i;
                    break;
                }
            }
            
            // If we don't have the serial number then add it
            if (curItem == null) {
                mItems.add(item);
            } else { // Combine the current EndItem to have the most up to date information
                curItem.combine(item);
            }
        } else { // If it is not serialized then just add it.
            mItems.add(item);
        }
    }
    
    public void sortBy(ORDER_BY order) {
        if (order == null) return;
        FXCollections.sort(mItems, order.getComparator());
    }
    
    ///////////////////////////////////////////////////////////////////////////////////
    /////// Properties
    ///////////////////////////////////////////////////////////////////////////////////

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
        return lINProperty().isEqualTo(item.lINProperty()).get()
                && nSNProperty().isEqualTo(item.nSNProperty()).get()  && getSerialized() == item.getSerialized();
    }
    
    @Override
    public int hashCode() {
        // Use the properties to generate hascode
        return 17 + 3 * lINProperty().hashCode() 
                + 7 * nSNProperty().hashCode();
    }
    
    @Override
    public String toString() {
        String name = "EndItemGroup " + getName() + " NSN:" + getNSN() + " LIN:" + getLIN();
        return name;
    }

}
