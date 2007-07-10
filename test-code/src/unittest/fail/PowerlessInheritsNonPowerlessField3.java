package unittest.fail;

import org.joe_e.testlib.ContainsNonImmutable;

class A2 extends ContainsNonImmutable {}

class B2 extends A2 {}

class C2 extends B2 {}

interface PowerlessPlus2 extends org.joe_e.Powerless {}

public class PowerlessInheritsNonPowerlessField3 extends C2
										implements PowerlessPlus2 {
	final Exception e = null;
	final String s = null;
}