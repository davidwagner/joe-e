package unittest.fail;

import org.joe_e.*;

public class ImmutableInNonImmutable3 {
	static void f() {
		class B {
			int q;
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
