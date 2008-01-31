// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.array;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.lang.reflect.Array;

import org.joe_e.array.ByteArray.Builder;


/**
 * An immutable array of <code>int</code>.
 */
public final class IntArray extends PowerlessArray<Integer> {
    static private final long serialVersionUID = 1L;   
    
    private /* final */ transient int[] ints;

    private IntArray(int... ints) {
        // Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
        super(null);
        this.ints = ints;
    }
    
    /**
     * Constructs a {@link IntArray}.
     * @param ints each <code>int</code>
     */
    static public IntArray array(final int... ints) {
        return new IntArray(ints.clone());
    }
    
    // java.io.Serializable interface
    
    /*
     * Serialization hacks to prevent the contents from being serialized as a
     * mutable array.  This improves efficiency for projects that serialize
     * Joe-E objects using Java's serialization API by avoiding treatment of
     * immutable state as mutable.  These methods can otherwise be ignored.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        out.writeInt(ints.length);
        for (int c : ints) {
            out.writeInt(c);
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, 
    						                      ClassNotFoundException {
        in.defaultReadObject();

        final int length = in.readInt();
        ints = new int[length];
        for (int i = 0; i < length; ++i) {
            ints[i] = in.readInt();
        }
    }
    
    /*
     *  Methods that must be overriden, as the implementation in ConstArray
     *  would try to use arr, which is null.
     */
    
    // java.lang.Object interface
    
    /**
     * Test for equality with another object
     * @return true if the other object is a {@link ConstArray} with the same
     *         contents as this array
     */
    public boolean equals(final Object other) {
        if (other instanceof IntArray) {
            // Simple case: just compare intArr fields
            return Arrays.equals(ints, ((IntArray)other).ints);
        } else if (other instanceof ConstArray) {
            // Other array does not have contents in intArr:
            // check that length matches, and then compare elements one-by-one
            final ConstArray otherArray = (ConstArray)other;
            if (otherArray.length() != ints.length) {
                return false;
            }            
            for (int i = 0; i < ints.length; ++i) {
                final Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Integer) ||
                    ((Integer)otherElement).intValue() != ints[i]) {
                    return false;
                }
            }            
            return true;
        } else {
            // Only a ConstArray can be equal to a IntArray
            return false;
        }
    }

    /**
     * Computes a digest of the array for hashing.  The hash code is the same
     * as <code>Arrays.hashCode()</code> called on a Java array containing the
     * same elements.
     * @return a hash code based on the contents of this array
     */
    public int hashCode() {
        // Because wrappers for primitive types return the same hashCode as 
        // their primitive values, a IntArray has the same hashCode as a
        // ConstArray<Integer> with the same contents.
        return Arrays.hashCode(ints);
    }
    
    /**
     * Return a string representation of the array
     */    
    public String toString() { 
        return Arrays.toString(ints);
    }
    
    // org.joe_e.ConstArray interface

    /**
     * Gets the length of the array.
     */
    public int length() { 
        return ints.length;
    }
    
    /**
     * Creates a {@link Integer} for a specified <code>int</code>.
     * @param i position of the <code>int</code> to return
     * @throws ArrayIndexOutOfBoundsException <code>i</code> is out of bounds
     */
    public Integer get(int i) { 
        return ints[i]; 
    }
    
    /**
     * Return a mutable copy of the array
     * @param prototype prototype of the array to copy into
     * @return an array containing the contents of this <code>ConstArray</code>
     *     of the same type as <code>prototype</code>
     * @throws ArrayStoreException if an element cannot be stored in the array
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] prototype) {
        final int len = length();
        if (prototype.length < len) {
            final Class t = prototype.getClass().getComponentType(); 
            prototype = (T[])Array.newInstance(t, len);
        }
        
        for (int i = 0; i < len; ++i) {
            prototype[i] = (T) (Integer) ints[i];
        }
        return prototype;
    }
    
    /**
     * Creates a {@link IntArray} with an appended {@link Integer}.
     * @param newInt   the {@link Integer} to append
     * @throws NullPointerException <code>newInt</code> is null
     */
    public IntArray with(final Integer newInt) {
        return with(newInt.intValue());
    }
           
    /*
     * Convenience (more efficient) methods with int
     */
        
    /**
     * Gets the <code>int</code> at a specified position.
     * @param i position of the <code>int</code> to return
     * @throws ArrayIndexOutOfBoundsException <code>i</code> is out of bounds
     */
    public int getInt(final int i) { 
        return ints[i]; 
    }

    /**
     * Creates a mutable copy of the <code>int</code> array
     */
    public int[] toIntArray() {
        return ints.clone(); 
    }
    
    /** 
     * Creates a {@link IntArray} with an appended <code>int</code>.
     * @param newInt   the <code>int</code> to append
     */
    public IntArray with(final int newInt) {
        final int[] newInts = new int[ints.length + 1];
        System.arraycopy(ints, 0, newInts, 0, ints.length);
        newInts[ints.length] = newInt;
        return new IntArray(newInts);
    }

    /**
     * Return a new <code>IntArray</code> that contains the same elements
     * as this one excluding the element at a specified index
     * @param i the index of the element to exclude
     * @return  the new array
     */
    public IntArray without(final int i) {
        final int[] newArr = new int[ints.length - 1];
        System.arraycopy(ints, 0, newArr, 0, i);
        System.arraycopy(ints, i + 1, newArr, i, newArr.length - i);
        return new IntArray(newArr);
    }
    
    /**
     * A {@link IntArray} factory.
     */
    static public final class Builder extends PowerlessArray.Builder<Integer> {
        private int[] buffer;
        private int size;

        /**
         * Construct an instance with the default internal array length.
         */
        public Builder() {
            this(0);
        }
        
        /**
         * Construct an instance.
         * @param estimate  estimated array length
         */
        public Builder(int estimate) {
            buffer = new int[estimate > 0 ? estimate : 32];
            size = 0;
        }

        // ArrayBuilder<Integer> interface
        public void append(Integer newInt) {
            append ((int) newInt);
        }
        
        public void append(final Integer[] newInts) {
            append(newInts, 0, newInts.length);
        }      
        
        public void append(final Integer[] newInts, 
                          final int off, final int len) {
            int newSize = size + len;
            if (len < 0 || newSize < 0 || off + len > newInts.length) {
                throw new IndexOutOfBoundsException();
            }
            if (newSize > buffer.length) {
                int newLength = Math.max(newSize, 2 * buffer.length);
                System.arraycopy(buffer, 0, buffer = new int[newLength], 0,
                                 size);
            }
            
            for (int i = 0; i < len; ++i) {
                buffer[size + i] = newInts[off + i];
            }           
            size = newSize;
        }
        
        /**
         * Create a snapshot of the current content.
         */
        public IntArray snapshot() {
            final int[] arr;
            if (size == buffer.length) {
                arr = buffer;
            } else {
                arr = new int[size];
                System.arraycopy(buffer, 0, arr, 0, size);
            }
            return new IntArray(arr);
        }
        
        /*
         * Convenience (more efficient) methods with int
         */
        public void append(final int newInt) {
            if (size == buffer.length) {
                System.arraycopy(buffer, 0, buffer = new int[2 * size], 0,
                                 size);
            }
            buffer[size++] = newInt;
        }

        public void append(final int[] newInts) {
            append(newInts, 0, newInts.length);
        }      
        
        public void append(final int[] newInts, final int off, final int len) {
            int newSize = size + len;
            if (len < 0 || newSize < 0 || off + len > newInts.length) {
                throw new IndexOutOfBoundsException();
            }
            if (newSize > buffer.length) {
                int newLength = Math.max(newSize, 2 * buffer.length);
                System.arraycopy(buffer, 0, buffer = new int[newLength], 0,
                                 size);
            }
            System.arraycopy(newInts, off, buffer, size, len);
            size = newSize;
        }
    }
    
    /* If one only invokes static methods statically, this is sound, since
     * IntArray extends PowerlessArray<Integer> and thus this method is
     * only required to return something of a type covariant with
     * PowerlessArray.Builder<Integer>.  Unfortunately, this is not completely
     * sound because it is possible to invoke static methods on instances, e.g.
     * ConstArray.Builder<String> = (ConstArray (IntArray.array())).builder(),
     * allowing for heap pollution without an unchecked cast warning.
     * 
     * The only solution to this would be to completely de-genericize these
     * methods.
     */
    @SuppressWarnings("unchecked")
    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public static Builder builder(final int estimate) {
        return new Builder(estimate);
    }
}
