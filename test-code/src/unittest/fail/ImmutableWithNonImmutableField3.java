package unittest.fail;

import org.joe_e.*;

public class ImmutableWithNonImmutableField3 {
	static void f() {
		new Immutable() {
			class B {}
			
			final int f = 4;
			final String s = "";
			final B b = null;
			
			public String toString() {
				return (b == null) ? f + s : "b";
			}
		};
	}
}
