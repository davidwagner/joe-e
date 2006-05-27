// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * 
 * @author Adrian Mettler 
 */
package org.joe_e;

public class SelflessArray<T> implements Selfless, Iterable<T> {
	private final T[] arr;
	
	public SelflessArray (T... arr) {
		this.arr = arr.clone();
	}
	
	public T at(int pos) {
		return arr[pos];
	}
	
	public int length() {
		return arr.length;
	}

	public T[] toArray() {
		return arr.clone();
	}

	public ArrayIterator<T> iterator() {
		return new ArrayIterator<T>(this);
	}
	
	/*
	 *  Example of an additional method to allow use as an abstraction for sets
	 *  or lists . . . should be added to subclasses too, if we want it; otherwise
	 *  adding a new element would downgrade them to a ConstArray.
	 */
	public SelflessArray<T> with(T newt) {
		Class componentType = arr.getClass().getComponentType();
		// The following line generates a type-soundness warning.
		T[] newArr = (T[]) 
			java.lang.reflect.Array.newInstance(componentType, arr.length + 1);
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = newt;
		return new SelflessArray<T>(newArr);
	}
}
