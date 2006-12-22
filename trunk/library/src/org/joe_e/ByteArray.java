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
 * An immutable array of byte.
 */
public class ByteArray extends PowerlessArray<Byte> {
    static final long serialVersionUID = 20061222;   
    
    private transient byte[] byteArr;

    /**
     * Construct an immutable byte array with a copy of an existing byte array as
     * backing store.
     * 
     * @param byteArr the array to make an unmodifiable duplicate of
     */
    public ByteArray(byte... byteArr) {
	// Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
	super();
        
        this.byteArr = byteArr.clone();
    }
    
    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  These methods can otherwise be ignored.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(byteArr.length);
        for (byte x : byteArr) {
            out.writeByte(x);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, 
    						      ClassNotFoundException {
        in.defaultReadObject();

        int length = in.readInt();
        byteArr = (byte[]) 
                  java.lang.reflect.Array.newInstance(Byte.class, length);
        for (int i = 0; i < length; ++i) {
            byteArr[i] = in.readByte();
        }
    }
        
    /**
     * Return the byte located at a specified position
     * 
     * @param pos the position whose byte to return
     * 
     * @return the byte at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public byte getByte(int pos) {
	return byteArr[pos];
    }

    /**
     * Return a mutable copy of the byte array
     * 
     * @return a mutable copy of the array
     */
    public byte[] toByteArray() {
	return byteArr.clone();
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
        return byteArr.length;
    }

    
    /**
     * Return a Byte containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Byte containing the byte at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
    public Byte get(int pos) {
	return byteArr[pos];
    }
	
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a ConstArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof ByteArray) {
            // Simple case: just compare byteArr fields
            ByteArray otherByteArray = (ByteArray) other;
            return Arrays.equals(byteArr, otherByteArray.byteArr);
        } else if (other instanceof ConstArray) {
	    // Other array does not have contents in byteArr:
	    // check that length matches, and then compare elements one-by-one
            ConstArray otherArray = (ConstArray) other;
            if (otherArray.length() != byteArr.length) {
                return false;
            }
            
            for (int i = 0; i < byteArr.length; ++i) {
                Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Byte) ||
                    (Byte) otherElement != byteArr[i]) {
                    return false;
                }
            }
            
            return true;
        } else {
            // Only ConstArrays can be equal to a ByteArray
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
        // their primitive values, a ByteArray has the same hashCode as a
        // ConstArray<Byte> with the same contents.
        return Arrays.hashCode(byteArr);
    }
    
    /**
     * Return a string representation of the array
     * 
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(byteArr);
    }
    
    /**
     * Return a mutable Byte array copy of the byte array
     * 
     * @return a mutable Byte array copy of the array
     */
    public Byte[] toArray() {
	Byte[] boxedArray = new Byte[byteArr.length];
	for (int i = 0; i < byteArr.length; ++i) {
	    boxedArray[i] = byteArr[i];
	}
	return boxedArray;
    }  
    
    
    /** 
     * Return a new ByteArray containing a specified additional byte
     * 
     * @return a new ByteArray containing a specified additional byte
     */
    public ByteArray with(byte newByte) {
        byte[] newArr = new byte[byteArr.length + 1];
        System.arraycopy(byteArr, 0, newArr, 0, byteArr.length);
        newArr[byteArr.length] = newByte;
        return new ByteArray(newArr);
    }
    
    /**
     * Return a new ByteArray containing a specified additional Byte
     * 
     * @return a new ByteArray containing a specified additional Byte
     */
    public ByteArray with(Byte newByte) {
	return with(newByte.byteValue());
    }
}
