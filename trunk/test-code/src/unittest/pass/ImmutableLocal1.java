package unittest.pass;

import org.joe_e.*;

public class ImmutableLocal1 {
	static class Inner implements Powerless {
		class MoreInner implements Powerless {
			void method() {
				final Token t = new Token();
				
				new Immutable() {
					public String toString() {
						if (t == null) {
							return "a";
						} else {
							return "b";
						}
					}
				};
			}
		}
	}
}
