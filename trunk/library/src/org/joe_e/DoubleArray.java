// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Arrays;

/**
 * An immutable array of double.
 */
public class DoubleArray extends PowerlessArray<Double> {
    static final long serialVersionUID = -9084610698309158874L;   
    
	private final double[] doubleArr;

	/**
	 * Construct an immutable double array with a copy of an existing double array as
	 * backing store.
	 * 
	 * @param doubleArr the array to make an unmodifiable duplicate of
	 */
	public DoubleArray(double... doubleArr) {
		// Use back door constructor that sets backing store to null.
        // This lets RecordArray's methods know not to use the backing
        // store for accessing this object.
	    super();
        
        this.doubleArr = doubleArr.clone();
   	}
    
    /**
     * Return the double located at a specified position
     * 
     * @param pos the position whose double to return
     * 
     * @return the double at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public double getDouble(int pos) {
		return doubleArr[pos];
	}

    /**
     * Return a mutable copy of the double array
     * 
     * @return a mutable copy of the array
     */
	public double[] toDoubleArray() {
		return doubleArr.clone();
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
        return doubleArr.length;
    }

    
    /**
     * Return a Double containing the value located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return a new Double containing the double at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public Double get(int pos) {
		return doubleArr[pos];
	}
	
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a RecordArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (other instanceof DoubleArray) {
            DoubleArray otherDoubleArray = (DoubleArray) other;
            return Arrays.equals(doubleArr, otherDoubleArray.doubleArr);
        } else if (other instanceof ConstArray) {
            ConstArray otherArray = (ConstArray) other;
            if (otherArray.length() != doubleArr.length) {
                return false;
            }
            for (int i = 0; i < doubleArr.length; ++i) {
                Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Double) ||
                    (Double) otherElement != doubleArr[i]) {
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
        // their primitive values, a DoubleArray has the same hashCode as a
        // RecordArray<Double>.
        return Arrays.hashCode(doubleArr);
    }
    
    /**
     * Return a string representation of the array
     * 
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(doubleArr);
    }
    
    /**
     * Return a mutable Double array copy of the double array
     * 
     * @return a mutable Double array copy of the array
     */
	public Double[] toArray() {
		Double[] boxedArray = new Double[doubleArr.length];
		for (int i = 0; i < doubleArr.length; ++i) {
			boxedArray[i] = doubleArr[i];
		}
		return boxedArray;
	}  
    
    
    /** 
     * Return a new DoubleArray containing a specified additional double
     * 
     * @return a new DoubleArray containing a specified additional double
     */
    public DoubleArray with(double newDouble) {
        double[] newArr = new double[doubleArr.length + 1];
        System.arraycopy(doubleArr, 0, newArr, 0, doubleArr.length);
        newArr[doubleArr.length] = newDouble;
        return new DoubleArray(newArr);
    }
    
    /**
     * Return a new DoubleArray containing a specified additional Double
     * 
     * @return a new DoubleArray containing a specified additional Double
     */
	public DoubleArray with(Double newDouble) {
		return with(newDouble.doubleValue());
	}
}
