package unittest.fail;

import org.joe_e.Powerless;

class Thingie3 {
	transient int[] q = null;
}

public class PowerlessInheritsNonFinalField2 extends Thingie3
										implements Powerless {
	final Exception e = null;
	final String s = null;
}

