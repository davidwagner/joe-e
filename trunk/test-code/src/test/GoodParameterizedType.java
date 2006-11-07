 package test;

import org.joe_e.Immutable;
import org.joe_e.Token;
import org.joe_e.Powerless;

// Actually, not so good: type parameter bounds are not sufficiently checked!
// Should fail the Immutable auditor.
public class GoodParameterizedType<I extends Powerless, T extends ExtendsToken> implements Immutable {
	final I inc;
	final T tok;
	
	GoodParameterizedType(I inc, T tok) {
		this.inc = inc;
		this.tok = tok;
	}
	
	class foo<Q> {
		Q qq;
		I ii;
	}
}
