// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An immutable array containing elements of an arbitrary type.
 */
public class RecordArray<E> implements Record, Iterable<E> {
	private final E[] arr;
    
    /**
     * Construct an immutable array with a copy of an existing array as
     * backing store.
     * 
     * @param arr the array to make an unmodifiable duplicate of
     */
	public RecordArray (E... arr) {
		this.arr = arr.clone();
	}
    
    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
    RecordArray() {
        arr = null;
    }
    
    /**
     * Return the element located at a specified position
     * 
     * @param pos the position whose contents to return
     * 
     * @return the element at the specified position
     * 
     * @throws ArrayIndexOutOfBoundsException if the specified position is
     * out of bounds.
     */
	public E get(int pos) {
		return arr[pos];
	}
     
    /**
     * Return the length of the array
     * 
     * @return the length of the array
     */
	public int length() {
		return arr.length;
	}

    /**
     * Return a mutable copy of the array
     * 
     * @return a mutable copy of the array
     */
	public E[] toArray() {
		return arr.clone();
	}

    /**
     * Return a new iterator over the array
     * 
     * @return the iterator over the array
     */
	public Iterator<E> iterator() {
		return new ArrayIterator<E>(this);
	}
       
    /**
     * Test for equality with another object
     * 
     * @return true if the other object is a RecordArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        if (!(other instanceof RecordArray)) {
            return false;
        }
        RecordArray otherArray = (RecordArray) other;
        if (otherArray.arr == null) {
            if (arr.length != otherArray.length()) {
                return false;
            }
            for (int i = 0; i < arr.length; ++i) {
                if (!arr[i].equals(otherArray.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return Arrays.equals(arr, otherArray.arr);
        }
    }

    /**
     * Computes a digest of the array for hashing
     * 
     * @return a hash code based on the contents of this array
     */
    public int hashCode() {
        return Arrays.hashCode(arr);
    }
        
    /*
     * Example of an additional method to allow use as an abstraction for sets
     * or lists . . . should be added to subclasses too, if we want it; otherwise
     * adding a new element would downgrade them to a RecordArray.
     *  
     * NOT PART OF AN ENDORSED STABLE INTERFACE! ... (yet)
     *  
     * Return a new RecordArray containing a specified additional element
     * 
     * @return a new RecordArray containing a specified additional element
     */
	public RecordArray<E> with(E newt) {
		Class componentType = arr.getClass().getComponentType();
		// The following line generates a type-soundness warning.
		E[] newArr = (E[]) 
			java.lang.reflect.Array.newInstance(componentType, arr.length + 1);
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = newt;
		return new RecordArray<E>(newArr);
	}
}
