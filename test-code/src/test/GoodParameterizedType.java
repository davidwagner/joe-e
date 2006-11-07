 package test;

import org.joe_e.Immutable;
import org.joe_e.Token;
import org.joe_e.Powerless;

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
