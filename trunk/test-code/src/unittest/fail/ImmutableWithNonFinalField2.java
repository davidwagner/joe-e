package unittest.fail;

import org.joe_e.*;

public class ImmutableWithNonFinalField2 {
	static void f() {
		new Immutable() {
			class B {}
			
			final int f = 4;
			transient String s = "";
			
			public String toString() {
				return f + s;
			}
		};
	}
}
