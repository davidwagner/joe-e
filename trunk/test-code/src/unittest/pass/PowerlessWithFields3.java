package unittest.pass;

import org.joe_e.Powerless;

public class PowerlessWithFields3<X extends Exception> implements Powerless {
	// erased type is Exception, which satisfies Powerless.  Boo-ya!
	final X xavier;
	
	PowerlessWithFields3(X x) {
		xavier = x;
	}
	
	void poohBear() throws X {
		throw xavier;
	}
}
