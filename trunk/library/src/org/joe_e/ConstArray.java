// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Arrays;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * An immutable array containing elements of an arbitrary type.
 *
 * <P>Note: this class implements Serializable in order to avoid preventing
 * trusted (non-Joe-E) code from serializing it.  The Java Serialization API
 * is tamed away as unsafe, and thus is not available to Joe-E code.
 */
public class ConstArray<E> implements Selfless, Iterable<E>, java.io.Serializable {
    static final long serialVersionUID = 624781170963430746L;
    
    // Marked transient to hide from serialization; see comment below constructor.
    // This field is really final, I swear.
    transient E[] arr;
    
    /**
     * Construct an immutable array with a copy of an existing array as
     * backing store.
     * 
     * @param arr the array to make an unmodifiable duplicate of
     */
    public ConstArray (E... arr) {
	this.arr = arr.clone();
    }

    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
    ConstArray() {
        arr = null;
    }    
    
    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  They can otherwise be ignored.
     */
    private void
    writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeObject(arr.getClass().getComponentType());
        out.writeInt(arr.length);
        for (Object x : arr) {
            out.writeObject(x);
        }
    }

    private void
    readObject(ObjectInputStream in) throws IOException,
                                                  ClassNotFoundException {
        in.defaultReadObject();

        Class component_type = (Class)in.readObject();
        int length = in.readInt();
        arr = (E[]) java.lang.reflect.Array.newInstance(component_type, length);
        for (int i = 0; i < length; ++i) {
            arr[i] = (E) in.readObject();
        }
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
    public ArrayIterator<E> iterator() {
	return new ArrayIterator<E>(this);
    }
       
    /**
     * Test for equality with another object.
     * 
     * @return true if the other object is a ConstArray with the same
     * contents as this array
     */ 
    public boolean equals(Object other) {
	// Can't be equal if not a ConstArray
	if (!(other instanceof ConstArray)) {
	    return false;
	}
	    
	ConstArray otherArray = (ConstArray) other;
	if (otherArray.arr == null) {
	    // Other array does not store contents in arr:
	    // check that length matches, and then compare elements one-by-one
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
	    // Other array stores contents in arr: just check for array
	    // equality between the two arr fields 
	    return (Arrays.equals(arr, otherArray.arr));
	}
    }

    /**
     * Computes a digest of the array for hashing
     * 
     * @return a hash code based on the contents of this array
     */
    public int hashCode() {
        int hashCode = 1;
        for (E i : arr) {
            if (i == null || Utility.instanceOf(i, Selfless.class)) {
                hashCode = 31 * hashCode + (i == null ? 0 : i.hashCode());
            } else {
                return arr.length * 31337;
            }
        }
        
        assert (hashCode == Arrays.hashCode(arr));
        return hashCode;
    }
    
    /**
     * Return a string representation of the array
     * 
     * TODO: Change this to support more types, either through
     * the introduction of an interface for toString()able objects
     * or through reflection.
     * 
     * @return a string representation of this array
     */    
    public String toString() {
	StringBuilder stringRep = new StringBuilder("[");
	for (int i = 0; i < arr.length; ++i) {
	    if (arr[i] == null) {
		stringRep.append("null");
	    }
	    if (arr[i] instanceof String || arr[i] instanceof ConstArray
		|| arr[i] instanceof Boolean || arr[i] instanceof Byte	
		|| arr[i] instanceof Character || arr[i] instanceof Double
		|| arr[i] instanceof Float || arr[i] instanceof Integer
		|| arr[i] instanceof Long || arr[i] instanceof Short) {
		stringRep.append(arr[i].toString());
	    } else {
		stringRep.append("<unprintable>");
	    }
	    
	    if (i + 1 < arr.length) {
		stringRep.append(", ");
	    }
	}
	
	return stringRep.append("]").toString();
    }
        
   /**
     * Return a new SelflessArray containing a specified additional element
     * 
     * @return a new SelflessArray containing a specified additional element
     */
    public ConstArray<E> with(E newt) {
	Class componentType = arr.getClass().getComponentType();
	// The following line generates a type-soundness warning.
	E[] newArr = (E[]) 
	    java.lang.reflect.Array.newInstance(componentType, arr.length + 1);
	System.arraycopy(arr, 0, newArr, 0, arr.length);
	newArr[arr.length] = newt;
	return new ConstArray<E>(newArr);
    }
}
