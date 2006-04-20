package org.joe_e;
/*
 * I'd really like to say T extends Incapable here, but then it wouldn't work for String, Integer, etc.,
 * without requiring one to use an IncapableCell.
 */
public class IncapableArray<T> extends DeepFrozenArray<T> {
	
	IncapableArray (T... arr) {
		if (!Utility.isSubtypeOf(arr.getClass().getComponentType(), Incapable.class)) {
			throw new IllegalArgumentException("Array type is not Incapable");
		}
	}
}
