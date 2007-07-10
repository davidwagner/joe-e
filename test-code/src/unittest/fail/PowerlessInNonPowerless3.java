package unittest.fail;

import org.joe_e.*;

public class PowerlessInNonPowerless3 {
	static void f() {
		class B {
			int q = 5;
			class C implements Powerless {		
				final int f = 4;
				final String s = "";
				
				public String toString() {
					return f + s + q;
				}
			};
		}
    }
}
