package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesT;

interface HasTTaker<T> {
	void takesT(T o);
}

// implements generic interface generically
public class DisabledMethodForInterface2<T> extends DisabledMethodTakesT<T>
										implements HasTTaker<T> {
	
}
