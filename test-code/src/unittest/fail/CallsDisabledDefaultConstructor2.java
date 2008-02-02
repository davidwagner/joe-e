package unittest.fail;

import org.joe_e.testlib.DisabledDefaultConstructor;

public class CallsDisabledDefaultConstructor2 {
	void foo () {
		new DisabledDefaultConstructor() {
			public boolean equals(Object other) {
				return ((Number) other).intValue() % 2 == 1;
			}
		};
	}
}
