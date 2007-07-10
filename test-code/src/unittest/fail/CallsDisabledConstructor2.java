package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class CallsDisabledConstructor2 {
	void foo () {
		new MembersDisabled() { };
	}
}
