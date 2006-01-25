package test;

import org.joe_e.DeepFrozen;
import org.joe_e.Token;
import org.joe_e.Incapable;

public class GoodParameterizedType<I extends Incapable, T extends ExtendsToken> implements DeepFrozen {
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
