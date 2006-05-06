package org.joe_e;

public class ConstArray<T> {
	T[] arr;
	
	ConstArray (T... arr) {
		this.arr = arr.clone();
	}
	
	T get(int index) {
		return arr[index];
	}
	
	int length() {
		return arr.length;
	}

	T[] getCopy() {
		return arr.clone();
	}

	/*
	 *  this is the only problematic method (?)
	 */
	ConstArray<T> with(T newt) {
		T[] newArr = (T[]) new Object[arr.length + 1];
		System.arraycopy(newt, 0, newArr, 0, arr.length);
		newArr[arr.length] = newt;
		return new ConstArray<T>(newArr);
	}
}
