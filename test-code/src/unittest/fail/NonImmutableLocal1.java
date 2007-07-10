package unittest.fail;

import org.joe_e.Immutable;

public class NonImmutableLocal1 {
	static void foop() {
		final int[] garbage = new int[4];
		class Gorbachev implements Immutable {
			public String toString() {
				return "" + garbage[0];
			}
		}
	}
}
