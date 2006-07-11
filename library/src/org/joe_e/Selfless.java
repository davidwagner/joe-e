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
 * a shallow copy of themselves.  Joe-E requries that classes that implement
 * this interface meet the obligation that all fields must be final and that
 * either (1) the superclass is selfless or (2) equals() and hashCode() are
 * overridden and super.equals() and super.hashCode() are not called.
 * 
 * This interface contains no members (?).
 */
public interface Selfless {
    /* doesn't matter whether these are here or not
    int hashCode();
    boolean equals(Object obj);
    */
}