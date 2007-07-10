package unittest.pass;

import org.joe_e.*;

public class ImmutableInImmutable3 {
	static void f() {
		class B implements org.joe_e.Powerless {
			final int q = 12;
			class C implements Immutable {		
				final int f = 4;
				final String s = "";
				
				public String toString() {
					return f + s + q;
				}
			}
		}
    }
}
