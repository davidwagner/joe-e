package unittest.pass;

import org.joe_e.testlib.ContainsNonImmutable;

public class AccessesEnabledField1 {
	Object o = ((ContainsNonImmutable) null).coordinates;
}
