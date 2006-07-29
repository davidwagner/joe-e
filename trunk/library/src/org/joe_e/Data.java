package org.joe_e;

/**
 * Marker interface for annotating classes all of whose contents must be 
 * (recursively) indistinguishable from a deep copy of an instance of the
 * class, and must be transparently constructable.
 * 
 * In addition to the restrictions imposed by the Record interface, all fields
 * of a class that implements Data must be of types statically declared to 
 * implement Data in the overlay type system.
 * 
 * This interface has no members.
 */
public interface Data extends Record {

}
