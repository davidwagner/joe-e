package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesString;

interface HasStringTaker<T> {
	void takesString(T o);
}

// implements generic interface generically but satisfies it non-generically
// Also, be tricky and define a constructor with the same name
public class DisabledMethodForInterface5 extends DisabledMethodTakesString
										implements HasStringTaker<String> {
}
