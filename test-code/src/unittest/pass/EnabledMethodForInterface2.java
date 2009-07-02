package unittest.pass;

import org.joe_e.testlib.EnabledMethodTakesT;

interface HasTTaker<T> {
	void takesT(T o);
}

// implements generic interface generically
public class EnabledMethodForInterface2<T> extends EnabledMethodTakesT 
										implements HasTTaker3<T> {

}
