package unittest.fail;

import org.joe_e.*;

public class ConstructNonPowerlessLocal1 {
	static class Inner implements Powerless {
		class MoreInner implements Powerless {
			void method() {
				final Token t = new Token();
				
				class NonPowerless {
					public String toString() {
						if (t == null) {
							return "a";
						} else {
							return "b";
						}
					}
				}
				
				new Powerless() {
					public String toString() {
						return new NonPowerless().toString();
					}
				};
			}
		}
	}
}
