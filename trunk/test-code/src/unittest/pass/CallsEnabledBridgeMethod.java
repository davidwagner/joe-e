package unittest.pass;

import org.joe_e.testlib.EnabledMethodTakesT;

class DefinesEnabledBridgeMethod extends EnabledMethodTakesT<String> {
	
}

public class CallsEnabledBridgeMethod {
	{
		((DefinesEnabledBridgeMethod) null).takesT("foo");
	}
}
