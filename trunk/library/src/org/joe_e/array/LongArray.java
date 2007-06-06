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


/**
 * An immutable array of <code>long</code>.
 */
public final class LongArray extends PowerlessArray<Long> {
    static private final long serialVersionUID = 1L;   
    
    private /* final */ transient long[] longs;

    private LongArray(long... longs) {
        // Use back door constructor that sets backing store to null.
        // This lets ConstArray's methods know not to use the backing
        // store for accessing this object.
        super(null);
        this.longs = longs;
    }
    
    /**
     * Constructs a {@link LongArray}.
     * @param longs each <code>long</code>
     */
    static public LongArray array(final long... longs) {
        return new LongArray(longs.clone());
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

        out.writeInt(longs.length);
        for (long c : longs) {
            out.writeLong(c);
        }
    }

    private void readObject(final ObjectInputStream in) throws IOException, 
    						                      ClassNotFoundException {
        in.defaultReadObject();

        final int length = in.readInt();
        longs = new long[length];
        for (int i = 0; i < length; ++i) {
            longs[i] = in.readLong();
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
        if (other instanceof LongArray) {
            // Simple case: just compare longArr fields
            return Arrays.equals(longs, ((LongArray)other).longs);
        } else if (other instanceof ConstArray) {
            // Other array does not have contents in longArr:
            // check that length matches, and then compare elements one-by-one
            final ConstArray otherArray = (ConstArray)other;
            if (otherArray.length() != longs.length) {
                return false;
            }            
            for (int i = 0; i < longs.length; ++i) {
                final Object otherElement = otherArray.get(i);
                if (!(otherElement instanceof Long) ||
                    ((Long)otherElement).longValue() != longs[i]) {
                    return false;
                }
            }            
            return true;
        } else {
            // Only a ConstArray can be equal to a LongArray
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
        // their primitive values, a LongArray has the same hashCode as a
        // ConstArray<Long> with the same contents.
        return Arrays.hashCode(longs);
    }
    
    /**
     * Return a string representation of the array
     */    
    public String toString() { 
        return Arrays.toString(longs);
    }
    
    // org.joe_e.ConstArray interface

    /**
     * Gets the length of the array.
     */
    public int length() { 
        return longs.length;
    }
    
    /**
     * Creates a {@link Long} for a specified <code>long</code>.
     * @param i position of the <code>long</code> to return
     * @throws ArrayIndexOutOfBoundsException <code>i</code> is out of bounds
     */
    public Long get(int i) { 
        return longs[i]; 
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
            prototype[i] = (T) (Long) longs[i];
        }
        return prototype;
    }
    
    /**
     * Creates a {@link LongArray} with an appended {@link Long}.
     * @param newLong   the {@link Long} to append
     * @throws NullPointerException <code>newLong</code> is null
     */
    public LongArray with(final Long newLong) {
        return with(newLong.longValue());
    }
           
    /*
     * Convenience (more efficient) methods with long
     */
        
    /**
     * Gets the <code>long</code> at a specified position.
     * @param i position of the <code>long</code> to return
     * @throws ArrayIndexOutOfBoundsException <code>i</code> is out of bounds
     */
    public long getLong(final int i) { 
        return longs[i]; 
    }

    /**
     * Creates a mutable copy of the <code>long</code> array
     */
    public long[] toLongArray() {
        return longs.clone(); 
    }
    
    /** 
     * Creates a {@link LongArray} with an appended <code>long</code>.
     * @param newLong   the <code>long</code> to append
     */
    public LongArray with(final long newLong) {
        final long[] newLongs = new long[longs.length + 1];
        System.arraycopy(longs, 0, newLongs, 0, longs.length);
        newLongs[longs.length] = newLong;
        return new LongArray(newLongs);
    }
}
