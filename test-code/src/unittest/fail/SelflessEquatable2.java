package unittest.fail;

import org.joe_e.*;

class SelflessType implements Selfless {
	public int hashCode() {
		return 0;
	}	
	public boolean equals(Object o) {
		return o instanceof SelflessType;
	}
}

interface EquatablePlus extends Equatable {
	boolean identical(Object o);
}

public class SelflessEquatable2 extends SelflessType 
	implements EquatablePlus {
	
	public boolean identical(Object o) {
		return this == o;
	}
}
