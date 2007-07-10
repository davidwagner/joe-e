package unittest.pass;

import org.joe_e.Immutable;

class Outer implements Immutable {
	class Inner {
		
		
		final Exception e = null;
		final String s = null;
	}
}

public class ImmutableInheritsImmutableParent1 extends Outer.Inner
										implements Immutable {
	ImmutableInheritsImmutableParent1(Outer out) {
		out.super();
	}
}
