package unittest.fail;

import org.joe_e.*;

public class SelflessEquatable1 implements Selfless, Equatable {
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object o) {
		return o instanceof SelflessEquatable1;
	}
}
