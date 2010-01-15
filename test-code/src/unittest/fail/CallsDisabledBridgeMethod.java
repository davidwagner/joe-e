package unittest.fail;

import org.joe_e.testlib.DisabledMethodTakesT;

class DefinesDisabledBridgeMethod extends DisabledMethodTakesT<String> {
	
}

public class CallsDisabledBridgeMethod {
	{
		((DefinesDisabledBridgeMethod) null).takesT("foo");
	}
}
