package test.verifier;

class OtherClass {
	void helpCheat() {
		cheat();
	}
	
	void cheat() {
		
	}
}

public class SuperMethodInvoker extends OtherClass {
	SuperMethodInvoker() {
		SuperMethodInvoker.super.helpCheat();
		cheat();
	}
	
	void cheat() {
		// could be passing off to a victim that sees a final field
		// uninitialized
		new Object().equals(this);
	}
}
