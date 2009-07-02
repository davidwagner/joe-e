package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesObject;

interface HasObjectTaker2<T> {
	void takesObject(T o);
}

// implements generic interface generically but satisfies it non-generically
// Also, be tricky and define a constructor with the same name
public class DisabledMethodForInterface4 {
	class takesObject<T> extends DisabledMethodTakesObject
										implements HasObjectTaker2<T> {
	takesObject(T o) {
		
	}
		
	}	
}
