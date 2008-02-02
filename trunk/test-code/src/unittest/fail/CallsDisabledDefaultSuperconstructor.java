package unittest.fail;

import org.joe_e.testlib.DisabledDefaultConstructor;

public class CallsDisabledDefaultSuperconstructor 
										extends DisabledDefaultConstructor {
	CallsDisabledDefaultSuperconstructor() {
		super();
		
		if (0 == 2) {
			Object o = new Object();
		}
	}
}
