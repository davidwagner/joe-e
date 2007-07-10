package unittest.fail;

import org.joe_e.Immutable;

public class InheritedNonImmutableLocal1 {
	static void foop(final int[] garbage) {
		class Kruschev {
			public String toString() {
				return "" + garbage[0];
			}
		}
		
		class Breshnev {
			Breshnev() {
				new Kruschev();
			}
		}
		
		class Gorbachev extends Breshnev implements Immutable {
			
		}
	}
}