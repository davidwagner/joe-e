package unittest.pass;

import org.joe_e.testlib.ContainsNonFinal;;

public class CallsEnabledDefaultSuperconstructor extends ContainsNonFinal {
	CallsEnabledDefaultSuperconstructor() {
		super();
		
		if (0 == 2) {
			Object o = new Object();
		}
	}
}
