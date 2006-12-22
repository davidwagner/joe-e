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
 * a shallow copy of themselves.
 * 
 * <P>Joe-E requires that classes that implement this interface must
 * meet all of the following obligations:
 * <OL>
 * <LI> All instance fields must be final.
 * <LI> The class cannot be equatable.
 * <LI> The object identity of instances of the class is not visible  This can
 *   be satisfied by either of:
 *   <OL>
 *   <LI> The superclass implements Selfless; or,
 *   <LI> The class's superclass is java.lang.Object, the class overrides
 *        <CODE>equals()</CODE>, and doesn't call <CODE>super.equals()</CODE>. 
 *   </OL>
 * <LI> The object provides a determinstic hash code.  This requirement is
 *   enforced by including hashCode() in this file.
 * </OL>
 * 
 * <P>The Joe-E verifier ensures that Joe-E code cannot distinguish a
 * shallow copy of a Selfless object from the original object.
 *  
 * <P>This interface includes the method <CODE>hashCode()</CODE>.
 * 
 * @see Equatable
 */
public interface Selfless {
    int hashCode();
    /*
    boolean equals(Object obj);
    */
}