package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesObject;

interface HasObjectTaker<T> {
	void takesObject(T o);
}

// implements generic interface non-generically
public class DisabledMethodForInterface2 extends DisabledMethodTakesObject
										implements HasObjectTaker {
	
}
