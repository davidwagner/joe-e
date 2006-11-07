package test;

import org.joe_e.Powerless;
import org.joe_e.Immutable;
import org.joe_e.Token;

public class BadDeepFrozen implements Immutable {
	static Powerless foo;
	final String qzar;
	final Token fooTok;
	final ExtendsToken barTok;
	final int[] foop = {1, 2, 3};
	
	BadDeepFrozen(Powerless foo2, String qzar, ExtendsToken et) {
	   this.qzar = qzar;
	   fooTok = barTok = et;
	}
}
