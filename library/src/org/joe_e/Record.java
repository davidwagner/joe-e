// Copyright 2006 Regents of the University of California.  May be used 
// under the terms of the revised BSD license.  See LICENSING for details.
/** 
 * 
 * 
 * @author Adrian Mettler 
 */
package org.joe_e;

/**
 * Marker interface for annotating classes that are indistinguishable from
 * a shallow copy of themselves, and are transparently copyable.  Joe-E
 * requries that classes that implement this interface meet the following
 * obligations 
 * 1. All instance fields must be public and final.
 * 2. The class cannot be equatable.
 * 3. The object identity of elements of the class is not visible.  This
 *   can be satisfied by one of:
 *   (a) The superclass is selfless
 *   (b) The class overrides hashCode() and equals(),
 *       and doesn't call super.hashCode() or super.equals(). 
 * 4. A trivial constructor is provided (one that takes values for the fields
 *  and assigns them to the fields).
 *  
 * This interface contains no members (?).
 */
public interface Record {
    /* doesn't matter whether these are here or not
    int hashCode();
    boolean equals(Object obj);
    */
}
