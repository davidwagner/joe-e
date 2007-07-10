package unittest.fail;

import org.joe_e.Immutable;

class Outer {
	class Inner {
		
		
		final Exception e = null;
		final String s = null;
	}
}

public class ImmutableInheritsNonImmutableParent1 extends Outer.Inner
										implements Immutable {
	ImmutableInheritsNonImmutableParent1(Outer out) {
		out.super();
	}
}
