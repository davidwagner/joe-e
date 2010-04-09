package unittest.fail;

import org.joe_e.*;

public class NonPowerlessLocal3 {
	static class Inner implements Powerless {
		class MoreInner implements Powerless {
			void method(final Token t) {
				new Powerless() {
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
