package test;

import org.joe_e.Powerless;
import org.joe_e.Immutable;
import org.joe_e.Token;

public class BadDeepFrozen implements Immutable {
	Powerless foo;
	final Powerless foo2;
	final String qzar;
	final Token fooTok;
	final ExtendsToken barTok;
	
	BadDeepFrozen(Powerless foo2, String qzar, ExtendsToken et) {
		this.foo2 = foo2;
		this.qzar = qzar;
		fooTok = barTok = et;
	}
}
