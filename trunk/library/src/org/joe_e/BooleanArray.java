// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * An immutable array of boolean.
 */
public class BooleanArray extends PowerlessArray<Boolean> {
    static final long serialVersionUID = 20061222;   
    
    private transient boolean[] booleanArr;

    /**
     * Construct an immutable boolean array with a copy of an existing boolean array as
     * backing store.
     * 
     * @param booleanArr the array to make an unmodifiable duplicate of
     */
    public BooleanArray(boolean... booleanArr) {
	// Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
	super();
        
        this.booleanArr = booleanArr.clone();
    }
    
    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  These methods can otherwise be ignored.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(booleanArr.length);
        for (boolean x : booleanArr) {
            out.writeBoolean(x);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, 
    						      ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        booleanArr = (boolean[]) 
                  java.lang.reflect.Array.newInstance(Boolean.class, length);
        for (int i = 0; i < length; ++i) {
            booleanArr[i] = in.readBoolean();
        }
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
     *  Methods that must be overriden, as the implementation in ConstArray
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
     * @return true if the other object is a ConstArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof BooleanArray) {
            // Simple case: just compare booleanArr fields
            BooleanArray otherBooleanArray = (BooleanArray) other;
            return Arrays.equals(booleanArr, otherBooleanArray.booleanArr);
        } else if (other instanceof ConstArray) {
	    // Other array does not have contents in booleanArr:
	    // check that length matches, and then compare elements one-by-one
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
            // Only ConstArrays can be equal to a BooleanArray
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
        // ConstArray<Boolean> with the same contents.
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
