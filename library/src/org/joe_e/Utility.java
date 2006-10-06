// Copyright 2005-06 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * NOT AN ENDORSED STABLE INTERFACE! (yet)
 * 
 * Comments?
 */
public class Utility {

    /**
     * Tests whether the specified object belongs to the specified type in the
     * overlay type system. The equivalent of the Java <CODE>instanceof</CODE>
     * operator, for the overlay type system.  
     * 
     * @param obj  the object to test
     * @param type the type to test membership of
     * @return true if the specified object belongs to the specified type
     *  in the overlay type system.
     */
    static public boolean instanceOf(Object obj, Class<?> type) {
        return obj != null && isSubtypeOf(obj.getClass(), type);
    }

    /**
     * Tests whether the first class is a subtype of the second in the overlay
     * type system.  
     * 
     * @param c1 the potential subtype
     * @param c2 the potential supertype
     * @return true if the first argument is a subtype of the second in the
     *  overlay type system
     */
    // NOT A BUG: Doesn't trace transitive relationships in overlay type system
    static public boolean isSubtypeOf(Class<?> c1, Class<?> c2) {
        if (c2.isAssignableFrom(c1)) {
            return true;
        } else {
            return Honoraries.honorarilyImplements(c1, c2);
        }
   }

}
