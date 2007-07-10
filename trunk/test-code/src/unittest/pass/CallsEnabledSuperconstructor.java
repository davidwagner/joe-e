package unittest.pass;

import org.joe_e.testlib.DisabledVarargsConstructor2;;

public class CallsEnabledSuperconstructor extends DisabledVarargsConstructor2 {
	CallsEnabledSuperconstructor() {
		super(new Object());
		
		if (0 == 2) {
			Object o = new Object();
		}
	}
}
