package unittest.pass;

import org.joe_e.testlib.ContainsNonFinal;

public class CallsEnabledConstructor2 {
	void foo() {
		new ContainsNonFinal() { };
	}
}
