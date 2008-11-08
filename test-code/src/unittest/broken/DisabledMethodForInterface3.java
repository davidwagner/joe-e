package unittest.broken;

import org.joe_e.testlib.DisabledMethodTakesObject;

interface HasObjectTaker2<T> {
	void takesObject(T o);
}

/* I believe that the failure of isSubsignature to do the right thing here is
 * a bug in Eclipse.  I really don't want to fix this myself.
 */

// implements generic interface non-generically
public class DisabledMethodForInterface3 extends DisabledMethodTakesObject
										implements HasObjectTaker2<Object> {
	
}
