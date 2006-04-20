package org.joe_e;

public class ConstList<T> {
	private T head;
	private ConstList<T> tail;
	
	public ConstList(T datum, ConstList<T> next) {
		this.head = datum;
		this.tail = next;
	}
	
	public T head() {
		return head;
	}
	
	public ConstList<T> tail() {
		return tail;
	}
	
	public ConstList(T[] arr) {
		this(arr, 0);
	}
	
	private ConstList(T[] arr, int first) {
		if (arr.length == 0) {
			throw new IllegalArgumentException("no such thing as an empty list");
		}
		this.head = arr[first];
		if (arr.length == first + 1) {
			this.tail = null;
		} else {
			this.tail = new ConstList<T>(arr, first + 1);
		}
	}
}
