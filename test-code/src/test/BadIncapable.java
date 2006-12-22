package test;

import org.joe_e.Powerless;
import org.joe_e.Immutable;
import org.joe_e.Token;

public class BadIncapable extends ExtendsToken implements Powerless {
		// can't extend token
	final Token t;	 // non-incapable
	int foo;	 	 // non-final
	final int foo2;  // OK
	final String st; // OK
	String qzar;	 // non-final
	final Immutable df;	// non-incapable
	final Powerless[] ia; 	// non-incapable
	final Powerless inc;	// OK
	
	BadIncapable(int foo2, String st, Immutable df, Powerless[] ia) {
		super(foo2);
		this.t = new Token();
		this.foo2 = foo2;
		this.st = st;
		this.df = df;
		this.ia = ia;
		this.inc = this; // this escaping (actually no, but flagged anyway)
	}	
}
