package unittest.pass;

import org.joe_e.*;
import org.joe_e.testlib.ContainsPowerless;

class IDontBite extends ContainsPowerless {
	IDontBite() {
		super("I", "don't", "know", "why", "you", "say", "good-bye,", "I",
			  "say", "hello.");
		foo = new Token();
	}
	final Token foo;
}

class NeitherDoI extends IDontBite {
	final boolean b;
	NeitherDoI(boolean b) {
		this.b = b;
	}
}

public class ImmutableInheritsFields2 extends NeitherDoI implements Immutable {
	final Exception e = null;
	final String s = null;
	
	ImmutableInheritsFields2(int num) {
		super(num %2 == 0);
	}
}
