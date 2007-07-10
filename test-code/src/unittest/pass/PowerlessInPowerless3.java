package unittest.pass;

import org.joe_e.*;

class PowerlessException extends Exception implements Powerless {
	public static final long serialVersionUID = 1;
}

public class PowerlessInPowerless3 {
	static void f() {
		class B extends PowerlessException {
			public static final long serialVersionUID = 1;
			final int q = 5;
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
