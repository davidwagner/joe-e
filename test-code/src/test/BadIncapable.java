package test;

import org.joe_e.Incapable;
import org.joe_e.DeepFrozen;
import org.joe_e.Token;

public class BadIncapable extends ExtendsToken implements Incapable {
	final Token t;	 // non-incapable
	int foo;	 	 // non-final
	final int foo2;  // OK
	final String st; // OK
	String qzar;	 // non-final
	final DeepFrozen df;	// non-incapable
	final Incapable[] ia; 	// non-incapable
	final Incapable inc;	// OK
	
	BadIncapable(int foo2, String st, DeepFrozen df, Incapable[] ia) {
		super(foo2);
		this.t = new Token();
		this.foo2 = foo2;
		this.st = st;
		this.df = df;
		this.ia = ia;
		this.inc = this;
	}	
}
