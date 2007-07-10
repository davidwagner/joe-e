package unittest.fail;

import org.joe_e.Immutable;

class Thingie2 {
	transient int[] q = null;
}

public class ImmutableInheritsNonFinalField2 extends Thingie2
										implements Immutable {
	final Exception e = null;
	final String s = null;
}

