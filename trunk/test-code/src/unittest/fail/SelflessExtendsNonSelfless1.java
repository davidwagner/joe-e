package unittest.fail;

import org.joe_e.*;

class Bob3 {
	
}

interface SelflessPlus extends Selfless {
	String toString();
}

public class SelflessExtendsNonSelfless1 extends Bob3 implements SelflessPlus {
	static final long serialVersionUID = 1;
	
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object o) {
		return o instanceof SelflessExtendsNonSelfless1;
	}
	
	public String toString() {
		return "fooblibar";
	}
}
