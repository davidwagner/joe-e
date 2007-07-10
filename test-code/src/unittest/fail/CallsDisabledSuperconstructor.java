package unittest.fail;

import org.joe_e.testlib.MembersDisabled;

public class CallsDisabledSuperconstructor extends MembersDisabled {
	CallsDisabledSuperconstructor() {
		super();
		
		if (0 == 2) {
			Object o = new Object();
		}
	}
}
