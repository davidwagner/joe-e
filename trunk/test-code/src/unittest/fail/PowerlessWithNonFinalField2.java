package unittest.fail;

import org.joe_e.*;

public class PowerlessWithNonFinalField2 {
	static void f() {
		new Powerless() {
			class B {}
			
			final int f = 4;
			transient String s = "";
			
			public boolean equals(Object o) {
				return false;
			}
			
			public int hashCode() {
				return f + s.hashCode();
			}
		};
	}
}
