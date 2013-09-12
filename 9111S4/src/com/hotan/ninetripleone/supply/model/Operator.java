package com.hotan.ninetripleone.supply.model;

/**
 * Class that represents a single operator 
 * @author Michael Hotan, michael.hotan@gmail.com
 */
public class Operator {

    private final Rank mRank;
    
    private final String mFirst, mLast;
    
    public Operator(String first, String last, Rank rank) {
        if (first == null) 
            throw new IllegalArgumentException(Operator.class.getSimpleName() + "() Can't have null first name");
        if (last == null) 
            throw new IllegalArgumentException(Operator.class.getSimpleName() + "() Can't have null last name");
        if (rank == null) 
            throw new IllegalArgumentException(Operator.class.getSimpleName() + "() Can't have null Rank");
        
        mFirst = first;
        mLast = last;
        mRank = rank;
    }
    
    public Rank getRank() {
        return mRank;
    }

    public String getFirstName() {
        return mFirst;
    }
    
    public String getLastName() {
        return mLast;
    }
    
    @Override
    public String toString() {
        return mRank.toString() + " " + mFirst + " " + mLast;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!o.getClass().equals(getClass())) return false;
        Operator oper = (Operator)o;
        return mRank == oper.mRank && mFirst.equals(oper.mFirst) && mLast.equals(oper.mLast); 
    } 
    
    @Override
    public int hashCode() {
        return mRank.hashCode() + 7 * mFirst.hashCode() + 11 * mLast.hashCode(); 
    }
}
