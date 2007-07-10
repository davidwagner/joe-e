package unittest.pass;

import org.joe_e.*;

public class PowerlessLocal1 {
	static void foop() {
		final Powerless garbage = new Powerless() {};
		class Gorbachev implements Immutable {
			public String toString() {
				return "" + (garbage == null);
			}
		}
	}
}
