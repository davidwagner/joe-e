package unittest.pass;

import org.joe_e.*;

public class PowerlessLocal3 {
	static void foop(final Powerless garbage) {
		class Gorbachev implements Immutable {
			public String toString() {
				return "" + (garbage == null);
			}
		}
	}
}
