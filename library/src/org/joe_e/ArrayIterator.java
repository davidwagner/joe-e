// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {
	private final SelflessArray<E> arr;
	private int pos;
	private int length;
	
	public ArrayIterator(SelflessArray<E> arr) {
		this.arr = arr;
		pos = 0;
		length = arr.length();
	}
	
	public boolean hasNext() {
		return (pos < length);
	}
	
	public E next() {
		if (pos < length) {
			return arr.at(pos++);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
