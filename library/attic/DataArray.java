// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * An immutable array containing objects of type Data.
 * 
 * @param <E> the element type of objects contained in the array
 */
public class DataArray<E> extends PowerlessArray<E> implements Data {
    static final long serialVersionUID = 7586004987745699549L;
    
    /**
     * Construct an immutable array with a copy of an existing array with
     * Data element type as backing store.
     * 
     * @param arr the array to make an unmodifiable duplicate of
     */ 
	public DataArray (E... arr) {
		// could use a hack with package-scope constructor to avoid redundant
		// powerless check here as a minor optimization, but this is simpler,
		// and more obviously correct.
		super(arr); 
		
		Class arrType = arr.getClass().getComponentType();
		if (!Utility.isSubtypeOf(arrType, Data.class)) {
			throw new IllegalArgumentException("DataArray component type "
											   + arrType + " is not Data");
		}
	}
    
    /**
     * Package-scope back-door constructor for use by subclasses that
     * override all methods that make use of the field arr.  Nullity of arr is
     * used to distinguish between instances with which this class must interact
     * by using the public interface rather than through their arr field.
     */
    DataArray() {
        super();
    }
    
    /**
     * Return a new DataArray containing a specified additional element
     * 
     * @return a new DataArray containing a specified additional element
     */
    public DataArray<E> with(E newt) {
        Class componentType = arr.getClass().getComponentType();
        // The following line generates a type-soundness warning.
        E[] newArr = (E[]) 
            java.lang.reflect.Array.newInstance(componentType, arr.length + 1);
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = newt;
        return new DataArray<E>(newArr);
    }
}
