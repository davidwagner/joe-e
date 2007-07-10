package unittest.fail;

import org.joe_e.Immutable;
import org.joe_e.Powerless;

class Thingie {
	final Immutable i = null;
}

public class PowerlessInheritsNonPowerlessField2 extends Thingie
										implements Powerless {
	final Exception e = null;
	final String s = null;
}

