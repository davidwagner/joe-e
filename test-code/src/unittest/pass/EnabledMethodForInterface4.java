package unittest.pass;

import org.joe_e.testlib.EnabledMethodTakesObject;

interface HasTTaker4<T> {
	void takesObject(T o);
}

// implements generic interface generically but satisfies it non-generically
public class EnabledMethodForInterface4<T> extends EnabledMethodTakesObject 
										implements HasTTaker4<T> {

}
