package unittest.pass;

import org.joe_e.testlib.ContainsNonImmutable;

public class AccessesEnabledField3 {
	ContainsNonImmutable cni = null;
	Object o = cni.coordinates;
}
