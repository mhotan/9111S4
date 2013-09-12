package com.hotan.ninetripleone.supply.model;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A Container class that represents a ODA Team PropertyBook
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class PropertyBook {

    private static final Logger LOG = Logger.getLogger(PropertyBook.class.getSimpleName());

    /**
     * Reference to who 
     */
    private final Operator mSignee, mSigner;

    /**
     * Date Prepared
     */
    private final Date mDatePrepared;

    /**
     * UIC, DESC, and Team the property book belongs
     */
    private final String UIC, DESC, team;

    /**
     * Collection of all the EndItemGroups
     */
    private final ObservableList<EndItemGroup> mEndItemGroups;

    public PropertyBook(UnitLevelHandReceipt  unitHR, ComponentHandReceipt compHR) {
        if (unitHR == null) {
            throw new NullPointerException("Null Unit Hand Receipt");
        }
        if (compHR == null) {
            throw new NullPointerException("Null Unit Hand Receipt");
        }

        mSignee = unitHR.getWhoFrom();
        mSigner = unitHR.getWhoTo();
        mDatePrepared = unitHR.getDatePrepared();
        UIC = unitHR.getUIC();
        DESC = unitHR.getDESC();
        team = unitHR.getTeam();

        mEndItemGroups = FXCollections.observableArrayList();

        List<EndItemGroup> compGroups = compHR.getGroups();
        List<EndItemGroup> unitGroups = unitHR.getGroups();

        // Iterate through all the Groups in the component list
        // Make sure the Unit Hand receipt contains the EndItemGroup
        // Before adding to the property book.
        for (EndItemGroup group: compGroups) {
            if (unitGroups.contains(group))
                mEndItemGroups.add(group);
            else
                LOG.warning("Component Hand Receipt has group that the Unit HR does not have \n Group: " + group);
        }

        // Now add all the groups from the Unit HR 
        // Make sure we update the EndItemGroup
        for (EndItemGroup group: unitGroups) {
            int index = mEndItemGroups.indexOf(group);
            if (index == -1) 
                mEndItemGroups.add(group);
            else {
                EndItemGroup curGroup = mEndItemGroups.get(index);
                curGroup.combine(group);
            }
        }
        
        
    }

    /**
     * Checks if there is an EndItem Group with matcheing nsn and lin.
     * 
     * @param nsn NSN of the EndItem
     * @param lin Lin number of the EndItem
     * @return Whether or not there exist an EndItem group with the same nsn and lin
     */
    public boolean hasGroup(String nsn, String lin) {
        return getGroup(nsn, lin) != null;
    }

    /**
     * Returns EndItem group that has the same nsn and lin number. 
     * 
     * @param nsn NSN to find.
     * @param lin LIN to find.
     * @return EndItemGroup that represents the the nsn and lin inputted, or null if non are found.
     */
    public EndItemGroup getGroup(String nsn, String lin) {
        if (nsn == null || lin == null) return null;
        for (EndItemGroup group: mEndItemGroups) {
            if (group.getNSN().equals(nsn) && group.getLIN().equals(lin)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns a list of all the EndItemGroups
     * 
     * @return Unmodifiable list of the EndItemGroups
     */
    public ObservableList<EndItemGroup> getGroups() {
        return mEndItemGroups;
    }

    /**
     * Returns number of groups
     * 
     * @return number of EndItemGroups
     */
    public int size() {
        return mEndItemGroups.size();
    }

    /**
     * @return Person who signing over the collection of End Items
     */
    public Operator getSignee() {
        return mSignee;
    }

    /**
     * @return Person who is signing for the collection of End Items
     */
    public Operator getSigner() {
        return mSigner;
    }

    public String getUIC() {
        return UIC;
    }

    public String getDESC() {
        return DESC;
    }

    public String getTeam() {
        return team;
    }

    public Date getDatePrepared() {
        return mDatePrepared;
    }
}
