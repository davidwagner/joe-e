package unittest.pass;

import org.joe_e.*;

class Bob3 implements Selfless {
	public int hashCode() {
		return 0;
	}
	public boolean equals(Object o) {
		return o instanceof SelflessExtendsSelfless;
	}
	
	public String toString() {
		return "fooblibar";
	}	
}

interface SelflessPlus extends Selfless {
	String toString();
}

public class SelflessExtendsSelfless extends Bob3 implements SelflessPlus {
	static final long serialVersionUID = 1;

}
