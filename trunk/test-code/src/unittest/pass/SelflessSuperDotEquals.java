package unittest.pass;

import org.joe_e.Selfless;

class Freddy implements Selfless {
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object other) {
		return false;
	}
}

public class SelflessSuperDotEquals extends Freddy 
										implements org.joe_e.Selfless {
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(Object other) {
		return super.equals(other);
	}
}
