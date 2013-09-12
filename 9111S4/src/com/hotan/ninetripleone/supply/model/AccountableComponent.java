package com.hotan.ninetripleone.supply.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents an Accountable Component to an EndItem.
 * 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public abstract class AccountableComponent {

    private final StringProperty name, NSN;
    private final IntegerProperty authQty;
    
    private IntegerProperty onHandQty;
    
    protected AccountableComponent(String name, String nsn) {
        this(name, nsn, 0);
    }
    
    protected AccountableComponent(String name, String nsn, int qty) {
        this.name = new SimpleStringProperty(name);
        this.NSN = new SimpleStringProperty(nsn);
        this.authQty = new SimpleIntegerProperty(qty);
    }
    
    public void setOnHandQty(int qty) {
        onHandQtyProperty().set(qty);
    }
   
    public String getName() {
        return name.get();
    }
    
    public String getNSN() {
        return NSN.get();
    }
    
    public int getAuthQty() {
        return authQty.get();
    }
    
    public int getOnHandQty() {
        return onHandQtyProperty().get();
    }
    
    public IntegerProperty onHandQtyProperty() {
        if (onHandQty == null) 
            onHandQty = new SimpleIntegerProperty(0);
        return onHandQty;
    }
    
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }
    
    public ReadOnlyStringProperty NSNProperty() {
        return NSN;
    }
    
    public ReadOnlyIntegerProperty authQtyProperty() {
        return authQty;
    }
}
