package test;

import org.joe_e.Incapable;
import org.joe_e.DeepFrozen;
import org.joe_e.Token;

public class BadDeepFrozen implements DeepFrozen {
	Incapable foo;
	final Incapable foo2;
	final String qzar;
	final Token fooTok;
	final ExtendsToken barTok;
	
	BadDeepFrozen(Incapable foo2, String qzar, ExtendsToken et) {
		this.foo2 = foo2;
		this.qzar = qzar;
		fooTok = barTok = et;
	}
}
