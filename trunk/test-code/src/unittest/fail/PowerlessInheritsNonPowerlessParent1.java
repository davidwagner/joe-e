package unittest.fail;

import org.joe_e.Powerless;

class Outer4 {
	class Inner {
		
		
		final Exception e = null;
		final String s = null;
	}
}

public class PowerlessInheritsNonPowerlessParent1 extends Outer4.Inner
										implements Powerless {
	PowerlessInheritsNonPowerlessParent1(Outer4 out) {
		out.super();
	}
}
