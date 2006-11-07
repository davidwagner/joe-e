// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Arrays;

/**
 * An immutable array of boolean.
 */
public class BooleanArray extends PowerlessArray<Boolean> {
    static final long serialVersionUID = -7541507816291995903L;   
    
	private final boolean[] booleanArr;

	/**
	 * Construct an immutable boolean array with a copy of an existing boolean array as
	 * backing store.
	 * 
	 * @param booleanArr the array to make an unmodifiable duplicate of
	 */
	public BooleanArray(boolean... booleanArr) {
		// Use back door constructor that sets backing store to null.
        // This lets RecordArray's methods know not to use the backing
        // store for accessing this object.
	    super();
        
        this.booleanArr = booleanArr.clone();
   	}
    
    /**
     * Return the boolean located at a specified position
     * 
     * @param pos the position whose boolean to return
     * 
     * @return the boolean at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public boolean getBoolean(int pos) {
		return booleanArr[pos];
	}

    /**
     * Return a mutable copy of the boolean array
     * 
     * @return a mutable copy of the array
     */
	public boolean[] toBooleanArray() {
		return booleanArr.clone();
	}

	/*
	 *  Methods that must be overriden, as the implementation in RecordArray
     *  would try to use arr, which is null.
	 */
	
    /**
     * Return the length of the array
     * 
     * @return the length of the array
     */
    public int length() {
        return booleanArr.length;
    }

    
    /**
     * Return a Boolean containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Boolean containing the boolean at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public Boolean get(int pos) {
		return booleanArr[pos];
	}
	
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a RecordArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof BooleanArray) {
            BooleanArray otherBooleanArray = (BooleanArray) other;
            return Arrays.equals(booleanArr, otherBooleanArray.booleanArr);
        } else if (other instanceof ConstArray) {
            ConstArray otherArray = (ConstArray) other;
            if (otherArray.length() != booleanArr.length) {
                return false;
            }
            for (int i = 0; i < booleanArr.length; ++i) {
                Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Boolean) ||
                    (Boolean) otherElement != booleanArr[i]) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Computes a digest of the array for hashing
     * 
     * @return a hash code based on the contents of this array
     */
    public int hashCode() {
        // Because wrappers for primitive types return the same hashCode as 
        // their primitive values, a BooleanArray has the same hashCode as a
        // RecordArray<Boolean>.
        return Arrays.hashCode(booleanArr);
    }
    
    /**
     * Return a string representation of the array
     * 
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(booleanArr);
    }
    
    /**
     * Return a mutable Boolean array copy of the boolean array
     * 
     * @return a mutable Boolean array copy of the array
     */
	public Boolean[] toArray() {
		Boolean[] boxedArray = new Boolean[booleanArr.length];
		for (int i = 0; i < booleanArr.length; ++i) {
			boxedArray[i] = booleanArr[i];
		}
		return boxedArray;
	}  
    
    
    /** 
     * Return a new BooleanArray containing a specified additional boolean
     * 
     * @return a new BooleanArray containing a specified additional boolean
     */
    public BooleanArray with(boolean newBoolean) {
        boolean[] newArr = new boolean[booleanArr.length + 1];
        System.arraycopy(booleanArr, 0, newArr, 0, booleanArr.length);
        newArr[booleanArr.length] = newBoolean;
        return new BooleanArray(newArr);
    }
    
    /**
     * Return a new BooleanArray containing a specified additional Boolean
     * 
     * @return a new BooleanArray containing a specified additional Boolean
     */
	public BooleanArray with(Boolean newBoolean) {
		return with(newBoolean.booleanValue());
	}
}
