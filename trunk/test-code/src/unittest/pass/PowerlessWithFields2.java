package unittest.pass;

import org.joe_e.*;

public class PowerlessWithFields2 {
	static void f() {
		new Powerless() {
			class B {}
			
			final Error err = new Error();
			final int f = 4;
			final String s = "";
			final short sh = 3;
			final byte by = -2;
			
			public boolean equals(Object o) {
				return (err == null) && (o == null);
			}
			
			public int hashCode() {
				return f + sh + by + s.hashCode();
			}
		};
	}
}
