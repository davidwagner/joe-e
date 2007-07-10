package unittest.fail;

import org.joe_e.*;

public class NonPowerlessLocal1 {
	static class Inner implements Powerless {
		class MoreInner implements Powerless {
			void method() {
				final Token t = new Token();
				
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
