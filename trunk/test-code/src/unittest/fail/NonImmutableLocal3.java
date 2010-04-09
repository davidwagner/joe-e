package unittest.fail;

import org.joe_e.Immutable;

public class NonImmutableLocal3 {
	static void foop(final int[] garbage) {
		class Gorbachev implements Immutable {
			public String toString() {
				return "" + garbage[0];
			}
		}
	}
}
