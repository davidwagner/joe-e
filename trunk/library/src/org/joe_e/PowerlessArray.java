// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;
/*
 * I'd really like to say T extends Incapable here, but then it wouldn't work
 * for String, Integer, etc., without requiring one to use an IncapableCell.
 */
public class PowerlessArray<T> extends ImmutableArray<T> implements Powerless {
	
	public PowerlessArray (T... arr) {
		// could use a hack with package-scope constructor to avoid redundant
		// immutable check here as a minor optimization, but this is simpler,
		// and more obviously correct.
		super(arr); 
		
		Class arrType = arr.getClass().getComponentType();
		if (!Utility.isSubtypeOf(arrType, Powerless.class)) {
			throw new IllegalArgumentException("PowerlessArray component type "
											   + arrType + " is not Powerless");
		}
	}
}
