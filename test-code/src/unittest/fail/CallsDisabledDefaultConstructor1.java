package unittest.fail;

import org.joe_e.testlib.DisabledDefaultConstructor;

public class CallsDisabledDefaultConstructor1 {
	void foo () {
		new DisabledDefaultConstructor();
	}
}
