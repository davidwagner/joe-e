package unittest.pass;

import org.joe_e.testlib.EnabledMethodTakesT;

public class CallsEnabledGenericMethod {
	{
		((EnabledMethodTakesT<String>) null).takesT("foo");
	}
}
