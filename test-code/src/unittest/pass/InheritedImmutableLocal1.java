package unittest.pass;

import org.joe_e.Token;
import org.joe_e.Immutable;

public class InheritedImmutableLocal1 {
	static void foop() {
		final Token garbage = new Token();
		class Kruschev {
			public String toString() {
				return (garbage == null) ? "a" : "b";
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