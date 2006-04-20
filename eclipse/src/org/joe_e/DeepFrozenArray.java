package org.joe_e;

/*
 * I'd really like to say T extends DeepFrozen here, but then it wouldn't work for String, Integer, etc.,
 * without requiring one to use a DFCell.
 */
public class DeepFrozenArray<T> extends ConstArray<T> {	
	
	DeepFrozenArray (T... arr) {
		if (!Utility.isSubtypeOf(arr.getClass().getComponentType(), DeepFrozen.class)) {
			throw new IllegalArgumentException("Array type is not Deep Frozen");
		}
	}
}
