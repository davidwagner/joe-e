package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesT;

public class CallsDisabledGenericMethod {
	{
		((DisabledMethodTakesT<String>) null).takesT("foo");
	}
}
