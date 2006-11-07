// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Arrays;

/**
 * An immutable array of byte.
 */
public class ByteArray extends PowerlessArray<Byte> {
    static final long serialVersionUID = -2523058214080487043L;   
    
	private final byte[] byteArr;

	/**
	 * Construct an immutable byte array with a copy of an existing byte array as
	 * backing store.
	 * 
	 * @param byteArr the array to make an unmodifiable duplicate of
	 */
	public ByteArray(byte... byteArr) {
		// Use back door constructor that sets backing store to null.
        // This lets RecordArray's methods know not to use the backing
        // store for accessing this object.
	    super();
        
        this.byteArr = byteArr.clone();
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
	 *  Methods that must be overriden, as the implementation in RecordArray
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
     * @return true if the other object is a RecordArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof ByteArray) {
            ByteArray otherByteArray = (ByteArray) other;
            return Arrays.equals(byteArr, otherByteArray.byteArr);
        } else if (other instanceof ConstArray) {
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
        // RecordArray<Byte>.
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
