// Copyright 2006-08 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e.array;

import org.joe_e.Immutable;
import org.joe_e.JoeE;
import org.joe_e.reflect.Reflection;

/**
 * An immutable array containing immutable objects.
 * 
 * @param <E> the element type of objects contained in the array
 */
public class ImmutableArray<E> extends ConstArray<E> implements Immutable {	
    static private final long serialVersionUID = 1L;
    
    
    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
	ImmutableArray(Object[] arr) {
		super(arr);
	}
    
    /**
     * Construct a <code>ImmutableArray</code>.
     * @param values    each value
     * @throws ClassCastException if the runtime component type of 
     *     <code>values</code> is not immutable in the overlay type system
     */
    static public <T> ImmutableArray<T> array(final T... values) {
        final Class<?> e = values.getClass().getComponentType();
        if (!JoeE.isSubtypeOf(e, Immutable.class)) {
            throw new ClassCastException(Reflection.getName(e) +
                                         " is not Immutable");
        }
        return new ImmutableArray<T>(values.clone());
    }
    
    /* 
     * See the comment in ConstArray for why the following methods exist.
     */

    /**
     * Construct an empty <code>ConstArray</code>.
     */
    static public <T> ImmutableArray<T> array() {
        return new ImmutableArray<T>(new Object[]{});
    }  
    
    /**
     * Checks an array to see that all elements are immutable; if so wraps
     * it WITHOUT a defensive copy
     */
    static private <T> ImmutableArray<T> array2(final Object... values) {
        for (Object v: values) {
            Class<?> vType = v.getClass();
            if (!JoeE.isSubtypeOf(vType, Immutable.class)) {
                throw new ClassCastException(Reflection.getName(vType) +
                                             " is not Immutable");
            }
        }
        return new ImmutableArray<T>(values);
    }

    /**
     * Construct a <code>ConstArray</code> with one element.
     * @param value    the value
     */
    static public <T> ImmutableArray<T> array(final T value) {
        return array2(value);
    }

    /**
     * Construct a <code>ConstArray</code> with two elements.
     * @param value1    the first value
     * @param value2    the second value
     */
    static public <T> ImmutableArray<T> array(final T value1, final T value2) {
        return array2(value1, value2);
    }

    /**
     * Construct an <code>ImmutableArray</code> with three elements.
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     */
    static public <T> ImmutableArray<T> array(final T value1, final T value2, 
                                          final T value3) {
        return array2(value1, value2, value3);
    }

    /**
     * Construct an <code>ImmutableArray</code> with four elements.
     * @param value1    the first value
     * @param value2    the second value
     * @param value3    the third value
     * @param value4    the fourth value
     */
    static public <T> ImmutableArray<T> array(final T value1, final T value2, 
                                          final T value3, final T value4) {
        return array2(value1, value2, value3, value4);
    }
        
    /**
     * Return a new <code>PowerlessArray</code> that contains the same elements
     * as this one but with a new element added to the end.
     * @param newE an element to add
     * @return the new array
     * @throws ClassCastException if <code>newE</code> is not immutable 
     */ 
    public ImmutableArray<E> with(final E newE) {
        if (!JoeE.instanceOf(newE, Immutable.class)) {
            throw new ClassCastException(Reflection.getName(newE.getClass()) +
                                         "is not Immutable");
        }
        // We use a new Object array here, because we don't know the static type
        // of E that was used; it may not match the dynamic component type of
        // arr due to array covariance.
        final Object[] newArr = new Object[arr.length + 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = newE;
        return new ImmutableArray<E>(newArr);
    }
    
    /**
     * Return a new <code>ImmutableArray</code> that contains the same elements
     * as this one excluding the element at a specified index
     * @param i the index of the element to exclude
     * @return  the new array
     */
    public ImmutableArray<E> without(final int i) {
        final Object[] newArr = new Object[arr.length - 1];
        System.arraycopy(arr, 0, newArr, 0, i);
        System.arraycopy(arr, i + 1, newArr, i, newArr.length - i);
        return new ImmutableArray<E>(newArr);
    }
    
    
    /**
     * An {@link ImmutableArray} factory.
     */
    public static class Builder<E> extends ConstArray.Builder<E> {
        /**
         * Construct an instance with the default internal array length.
         */
        Builder() {
            super();
        }
        
        /**
         * Construct an instance.
         * @param estimate  estimated array length
         */
        Builder(int estimate) {
            super(estimate);
        }        

        /** 
         * Appends an element to the Array
         * @param newE the element to append
         * @throws ClassCastException if the <code>newE</code> is not immutable
         * @throws NegativeArraySizeException if the resulting internal array
         *  would exceed the maximum length of a Java array.  The builder is
         *  unmodified.
         */
         public void append(E newE) {
            if (!JoeE.instanceOf(newE, Immutable.class)) {
                throw new ClassCastException(Reflection.getName(newE.getClass())
                                             + "is not Immutable");
            }
            
            appendInternal(newE);
        }

        /** 
         * Appends all elements from a Java array to the Array
         * @param newEs the element to append
         * @throws ClassCastException if the <code>newEs</code> is not immutable
         * @throws IndexOutOfBoundsException if the resulting internal array
         *  would exceed the maximum length of a Java array.  The builder is
         *  unmodified. 
         */
        public void append(E[] newEs) {
            append(newEs, 0, newEs.length);
        }

        /** 
         * Appends a range of elements from a Java array to the Array
         * @param newEs the source array
         * @param off   the index of the first element to append
         * @param len   the number of elements to append
         * @throws ClassCastException if the <code>newEs</code> is not immutable
         * @throws IndexOutOfBoundsException if an out-of-bounds index would
         *  be referenced or the resulting internal array would exceed the
         *  maximum length of a Java array.  The builder is unmodified.
         */
        public void append(E[] newEs, int off, int len) {
            final Class<?> e = newEs.getClass().getComponentType();
            if (!JoeE.isSubtypeOf(e, Immutable.class)) {
                throw new ClassCastException(Reflection.getName(e) + 
                                             " is not Immutable");
            }
            
            appendInternal(newEs, off, len);
        }
        
        /**
         * Create a snapshot of the current content.
         * @return an <code>ImmutableArray<E></code> containing the elements so far
         */
        public ImmutableArray<E> snapshot() {
            final Object[] arr;
            if (size == buffer.length) {
                arr = buffer;
            } else {
                arr = new Object[size];
                System.arraycopy(buffer, 0, arr, 0, size);
            }
            return new ImmutableArray<E>(arr);
        }
    }   
    
    /**
     * Get an <code>ImmutableArray.Builder</code>.  This is equivalent to the
     * constructor.
     * @return a new builder instance, with the default internal array length
     */
    public static <E> Builder<E> builder() {
        return new Builder<E>(0);
    }
    
    /**
     * Get an <code>ImmutableArray.Builder</code>.  This is equivalent to the
     * constructor.
     * @param estimate  estimated array length  
     * @return a new builder instance
     */    
    public static <E> Builder<E> builder(final int estimate) {
        return new Builder<E>(estimate);
    }
}