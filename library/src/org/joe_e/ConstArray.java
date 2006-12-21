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

    /*
     * Serialization hacks to prevent the contents from being serialized as
     * a mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API to avoid treating immutable
     * state as mutable.  They can otherwise be ignored.
     */
    private void
    writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeObject(arr.getClass().getComponentType());
        out.writeInt(arr.length);
        for (final Object x : arr) {
            out.writeObject(x);
        }
    }

    private void
    readObject(final ObjectInputStream in) throws IOException,
                                                  ClassNotFoundException {
        in.defaultReadObject();

        final Class component_type = (Class)in.readObject();
        final int length = in.readInt();
        arr = (E[])java.lang.reflect.Array.newInstance(component_type, length);
        for (int i = 0; i != length; ++i) {
            arr[i] = (E)in.readObject();
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
     * Test for equality with another object
     * 
     * @return true if the other object is a SelflessArray with the same
     * contents as this array
     */
    public boolean equals(Object other) {
        return (other instanceof ConstArray &&
                Arrays.equals(arr, ((ConstArray) other).arr));
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
     * @return a string representation of this array
     */    
    public String toString() {
        return Arrays.toString(arr);
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
