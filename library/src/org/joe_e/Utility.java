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
    /*
     * It might be hard to believe at first that an algorithm this simple
     * can take into account all transitive dependencies correctly,
     * but here is the key fact that makes it work:
     * if C honorarily implements marker interface I, and D is a
     * subclass of C, then either (1) D is from the Java library,
     * in which case the honorary implementation guarantees that D
     * will also be marked as honorarily implementing I; or (2) D is
     * user code, in which case the Joe-E verifier requires D to explicitly
     * implement I (in the Java type system).  In either case, this
     * accounts for all transitive dependencies: in case (1), the call
     * to honorarilyImplements() take care of transitive subtyping;
     * in case (2), isAssignableFrom() takes care of it.
     */
    static public boolean isSubtypeOf(Class<?> c1, Class<?> c2) {
        if (c2.isAssignableFrom(c1)) {
            return true;
        } else {
            return Honoraries.honorarilyImplements(c1, c2);
        }
   }
}
