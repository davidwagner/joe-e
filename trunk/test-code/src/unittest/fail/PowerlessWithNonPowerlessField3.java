package unittest.fail;

import org.joe_e.*;

public class PowerlessWithNonPowerlessField3 {
	static void f() {
		new Powerless() {
			final int f = 4;
			final String s = "";
			final Token t = null;
			
			public String toString() {
				return (t == null) ? f + s : "b";
			}
		};
	}
}
