package unittest.fail;

import org.joe_e.*;

class Bob {
	
}

public class SelflessExtendsNonSelfless2 extends Bob implements Selfless {
	static final long serialVersionUID = 1;
	
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object o) {
		return o instanceof SelflessExtendsNonSelfless2;
	}
}
