// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator for SelflessArrays.  Needed in order for SelflessArray and its
 * subclasses to support the Iterable interface and be usable with the new 
 * for-loop syntax.
 *
 * @param <E> the element type of the SelflessArray being iterated
 */
public class ArrayIterator<E> implements Iterator<E> {
	private final SelflessArray<E> arr;
	private int pos;
	private final int length;
	
    /**
     * Create an ArrayIterator to iterate over the specified SelflessArray
     * @param arr the array to iterate over
     */
	public ArrayIterator(SelflessArray<E> arr) {
		this.arr = arr;
		this.pos = 0;
		this.length = arr.length();
	}
	
    /**
     * Returns true if the iteration has more elements. 
     * (In other words, returns true if next would return an element rather than throwing an exception.)
     *
     * @return true if the iterator has more elements.
     */
	public boolean hasNext() {
		return (pos < length);
	}
	
    /**
     * Returns the next element in the array.
     * 
     * @return the next element in the array.
     * @throws NoSuchElementException if the end of the array has been reached.
     */
    public E next() {
		if (pos < length) {
			return arr.at(pos++);
		} else {
			throw new NoSuchElementException();
		}
	}

    /**
     * Remove is not supported by this iterator.
     * 
     * @throws UnsupportedOperationException
     */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
