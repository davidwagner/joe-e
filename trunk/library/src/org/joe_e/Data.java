package org.joe_e;

/**
 * Marker interface for annotating classes all of whose contents must be 
 * (recursively) indistinguishable from a deep copy of an instance of the
 * class, and must be transparently constructable.  These objects have the
 * property that one can construct a deep copy of a Data object that is
 * indistinguishable from the original: in this way a Data object can be
 * worth no more than the information it contains.
 * 
 * <P>In addition to the restrictions imposed by the Record interface, all fields
 * of a class that implements Data must be of types statically declared to 
 * implement Data in the overlay type system.
 * 
 * <P>This interface has no members.
 * 
 * @see Record
 */
// BUG: Should extend Powerless, too.  --daw
public interface Data extends Record {

}
