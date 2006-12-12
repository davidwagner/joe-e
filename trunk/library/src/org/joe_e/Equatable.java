// Copyright 2005-06 Regents of the University of California.  May be used
// under the terms of the revised BSD license.  See LICENSING for details.
/**
 * @author Adrian Mettler
 */
package org.joe_e;

/**
 * Marker interface for annotating classes whose instances are permitted
 * to be compared using the <CODE>==</CODE> and <CODE>!=</CODE> operators.
 * These operators compare using the address of the object and thus expose
 * object identity.  Objects that do not implement Equatable are prohibited
 * (by Joe-E) from using these comparison operators.
 * 
 * <P>A class that implements Equatable must not implement Selfless.
 * 
 * <P>This interface has no members.
 * 
 * @see Selfless
 */


public interface Equatable {

}
