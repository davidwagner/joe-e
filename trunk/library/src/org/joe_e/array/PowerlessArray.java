// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.array;

import org.joe_e.JoeE;
import org.joe_e.Powerless;

/**
 * An immutable array containing powerless objects.
 * 
 * @param <E> the element type of objects contained in the array
 */
public class PowerlessArray<E> extends ImmutableArray<E> implements Powerless {
    static private final long serialVersionUID = 1L;

    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
	PowerlessArray(final Object[] arr) {
		super(arr); 
	}
    
    /**
     * Constuct a {@link PowerlessArray}.  The type will be 
     * @param values    each value, or an array of values
     * @throws ClassCastException if the runtime component type of 
     *     <code>values</code> is not powerless in the overlay type system
     */
    static public <E> PowerlessArray<E> array(final E... values) {
        final Class e = values.getClass().getComponentType();
        if (!JoeE.isSubtypeOf(e, Powerless.class)) {
            throw new ClassCastException(e.getName() + " is not Powerless");
        }
        return new PowerlessArray<E>(values.clone());
    }
    
    /**
     * Return a new <code>PowerlessArray</code> that contains the same elements
     * as this one but with a new element added to the end.
     * @param newE an element to add
     * @return the new array
     * @throws ClassCastException if <code>newE</code> is not powerless 
     */
    public PowerlessArray<E> with(E newE) {
        if (!JoeE.instanceOf(newE, Powerless.class)) {
            throw new ClassCastException(newE.getClass().getName() + "is not Powerless");
        }
        // We use a new Object array here, because we don't know the static type
        // of E that was used; it may not match the dynamic component type of
        // arr due to array covariance.
        final Object[] newArr = new Object[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = newE;
        return new PowerlessArray<E>(newArr);
    }
    
    /**
     * Return a new <code>PowerlessArray</code> that contains the same elements
     * as this one excluding the element at a specified index
     * @param i the index of the element to exclude
     * @return  the new array
     */
    public PowerlessArray<E> without(final int i) {
        final Object[] newArr = new Object[arr.length - 1];
        System.arraycopy(arr, 0, newArr, 0, i);
        System.arraycopy(arr, i + 1, newArr, i, newArr.length - i);
        return new PowerlessArray<E>(newArr);
    }
    
    public static class Builder<E> implements ArrayBuilder<E> {
        private Object[] buffer;
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
            buffer = new Object[estimate > 0 ? estimate : 32];
            size = 0;
        }        

        /** 
         * Appends an element to the Array
         * @param newE the element to append
         */
        public void write(E newE) {
            if (!JoeE.instanceOf(newE, Powerless.class)) {
                throw new ClassCastException(newE.getClass().getName() +
                                             "is not Powerless");
            }
            
            if (size == buffer.length) {
                System.arraycopy(buffer, 0, buffer = new Object[2 * size], 0,
                                 size);
            }
            buffer[size++] = newE;
        }

        /** 
         * Appends all elements from a Java array to the Array
         * @param newEs the element to append
         */
        public void write(E[] newEs) {
            write(newEs, 0, newEs.length);
        }

        /** 
         * Appends a range of elements from a Java array to the Array
         * @param newEs the source array
         * @param off   the index of the first element to append
         * @param len   the number of elements to append
         */
        public void write(E[] newEs, int off, int len) {
            final Class e = newEs.getClass().getComponentType();
            if (!JoeE.isSubtypeOf(e, Powerless.class)) {
                throw new ClassCastException(e.getName() + " is not Powerless");
            }
            
            int newSize = size + len;
            if (len < 0 || newSize < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (newSize > buffer.length) {
                int newLength = Math.max(newSize, 2 * buffer.length);
                System.arraycopy(buffer, 0, buffer = new Object[newLength], 0,
                                 size);
            }
            System.arraycopy(newEs, off, buffer, size, len);
            size = newSize;
        }
        
        /**
         * Create a snapshot of the current content.
         * @return a <code>PowerlesstArray<E></code> containing the elements written
         *         so far
         */
        public PowerlessArray<E> snapshot() {
            final Object[] arr;
            if (size == buffer.length) {
                arr = buffer;
            } else {
                arr = new Object[size];
                System.arraycopy(buffer, 0, arr, 0, size);
            }
            return new PowerlessArray<E>(arr);
        }
    } 
}
