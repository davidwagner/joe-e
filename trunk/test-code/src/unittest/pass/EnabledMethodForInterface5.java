package unittest.pass;

import org.joe_e.testlib.EnabledMethodTakesString;

interface HasStringTaker<T> {
	void takesString(T o);
}

// implements generic interface generically but satisfies it non-generically
// Also, be tricky and define a constructor with the same name
public class EnabledMethodForInterface5 extends EnabledMethodTakesString
										implements HasStringTaker<String> {
}
