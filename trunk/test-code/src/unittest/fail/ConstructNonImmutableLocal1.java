package unittest.fail;

import org.joe_e.Immutable;

public class ConstructNonImmutableLocal1 {
	static void foop() {
		final int[] garbage = new int[4];
		class Gorbachev {
			public String toString() {
				return "" + garbage[0];
			}
		}
		
		class Mikhail implements Immutable {
			{
				new Gorbachev();
			}
		}
	}
}
