package unittest.pass;

import org.joe_e.*;

public class Equatable1 implements Equatable {
	public int hashCode() {
		return 0;
	}
	
	public boolean equals(Object o) {
		return o instanceof Equatable1;
	}
}
