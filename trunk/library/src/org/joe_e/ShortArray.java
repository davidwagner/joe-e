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
 * An immutable array of short.
 */
public class ShortArray extends PowerlessArray<Short> {
    static final long serialVersionUID = 20061222;   
    
    private transient short[] shortArr;

    /**
     * Construct an immutable short array with a copy of an existing short array as
     * backing store.
     * 
     * @param shortArr the array to make an unmodifiable duplicate of
     */
    public ShortArray(short... shortArr) {
	// Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
	super();
        
        this.shortArr = shortArr.clone();
    }
    
    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  These methods can otherwise be ignored.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(shortArr.length);
        for (short x : shortArr) {
            out.writeShort(x);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, 
    						      ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        shortArr = (short[]) 
                  java.lang.reflect.Array.newInstance(Short.class, length);
        for (int i = 0; i < length; ++i) {
            shortArr[i] = in.readShort();
        }
    }
        
    /**
     * Return the short located at a specified position
     * 
     * @param pos the position whose short to return
     * 
     * @return the short at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public short getShort(int pos) {
	return shortArr[pos];
    }

    /**
     * Return a mutable copy of the short array
     * 
     * @return a mutable copy of the array
     */
    public short[] toShortArray() {
	return shortArr.clone();
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
        return shortArr.length;
    }

    
    /**
     * Return a Short containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Short containing the short at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public Short get(int pos) {
	return shortArr[pos];
    }
	
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a ConstArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof ShortArray) {
            // Simple case: just compare shortArr fields
            ShortArray otherShortArray = (ShortArray) other;
            return Arrays.equals(shortArr, otherShortArray.shortArr);
        } else if (other instanceof ConstArray) {
	    // Other array does not have contents in shortArr:
	    // check that length matches, and then compare elements one-by-one
            ConstArray otherArray = (ConstArray) other;
            if (otherArray.length() != shortArr.length) {
                return false;
            }
            
            for (int i = 0; i < shortArr.length; ++i) {
                Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Short) ||
                    (Short) otherElement != shortArr[i]) {
                    return false;
                }
            }
            
            return true;
        } else {
            // Only ConstArrays can be equal to a ShortArray
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
        // their primitive values, a ShortArray has the same hashCode as a
        // ConstArray<Short> with the same contents.
        return Arrays.hashCode(shortArr);
    }
    
    /**
     * Return a string representation of the array
     * 
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(shortArr);
    }
    
    /**
     * Return a mutable Short array copy of the short array
     * 
     * @return a mutable Short array copy of the array
     */
    public Short[] toArray() {
	Short[] boxedArray = new Short[shortArr.length];
	for (int i = 0; i < shortArr.length; ++i) {
	    boxedArray[i] = shortArr[i];
	}
	return boxedArray;
    }  
    
    
    /** 
     * Return a new ShortArray containing a specified additional short
     * 
     * @return a new ShortArray containing a specified additional short
     */
    public ShortArray with(short newShort) {
        short[] newArr = new short[shortArr.length + 1];
        System.arraycopy(shortArr, 0, newArr, 0, shortArr.length);
        newArr[shortArr.length] = newShort;
        return new ShortArray(newArr);
    }
    
    /**
     * Return a new ShortArray containing a specified additional Short
     * 
     * @return a new ShortArray containing a specified additional Short
     */
    public ShortArray with(Short newShort) {
	return with(newShort.shortValue());
    }
}
