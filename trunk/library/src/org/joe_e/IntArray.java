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
 * An immutable array of int.
 */
public class IntArray extends PowerlessArray<Integer> {
    static final long serialVersionUID = 20061222;   
    
    private transient int[] intArr;

    /**
     * Construct an immutable int array with a copy of an existing int array as
     * backing store.
     * 
     * @param intArr the array to make an unmodifiable duplicate of
     */
    public IntArray(int... intArr) {
	// Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
	super();
        
        this.intArr = intArr.clone();
    }
    
    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  These methods can otherwise be ignored.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(intArr.length);
        for (int x : intArr) {
            out.writeInt(x);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, 
    						      ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        intArr = (int[]) 
                  java.lang.reflect.Array.newInstance(Integer.class, length);
        for (int i = 0; i < length; ++i) {
            intArr[i] = in.readInt();
        }
    }
        
    /**
     * Return the int located at a specified position
     * 
     * @param pos the position whose int to return
     * 
     * @return the int at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public int getInt(int pos) {
	return intArr[pos];
    }

    /**
     * Return a mutable copy of the int array
     * 
     * @return a mutable copy of the array
     */
    public int[] toIntArray() {
	return intArr.clone();
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
        return intArr.length;
    }

    
    /**
     * Return a Integer containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Integer containing the int at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public Integer get(int pos) {
	return intArr[pos];
    }
	
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a ConstArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof IntArray) {
            // Simple case: just compare intArr fields
            IntArray otherIntArray = (IntArray) other;
            return Arrays.equals(intArr, otherIntArray.intArr);
        } else if (other instanceof ConstArray) {
	    // Other array does not have contents in intArr:
	    // check that length matches, and then compare elements one-by-one
            ConstArray otherArray = (ConstArray) other;
            if (otherArray.length() != intArr.length) {
                return false;
            }
            
            for (int i = 0; i < intArr.length; ++i) {
                Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Integer) ||
                    (Integer) otherElement != intArr[i]) {
                    return false;
                }
            }
            
            return true;
        } else {
            // Only ConstArrays can be equal to a IntArray
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
        // their primitive values, a IntArray has the same hashCode as a
        // ConstArray<Integer> with the same contents.
        return Arrays.hashCode(intArr);
    }
    
    /**
     * Return a string representation of the array
     * 
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(intArr);
    }
    
    /**
     * Return a mutable Integer array copy of the int array
     * 
     * @return a mutable Integer array copy of the array
     */
    public Integer[] toArray() {
	Integer[] boxedArray = new Integer[intArr.length];
	for (int i = 0; i < intArr.length; ++i) {
	    boxedArray[i] = intArr[i];
	}
	return boxedArray;
    }  
    
    
    /** 
     * Return a new IntArray containing a specified additional int
     * 
     * @return a new IntArray containing a specified additional int
     */
    public IntArray with(int newInt) {
        int[] newArr = new int[intArr.length + 1];
        System.arraycopy(intArr, 0, newArr, 0, intArr.length);
        newArr[intArr.length] = newInt;
        return new IntArray(newArr);
    }
    
    /**
     * Return a new IntArray containing a specified additional Integer
     * 
     * @return a new IntArray containing a specified additional Integer
     */
    public IntArray with(Integer newInt) {
	return with(newInt.intValue());
    }
}
