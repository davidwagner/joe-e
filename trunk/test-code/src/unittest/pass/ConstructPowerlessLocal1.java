package unittest.pass;

import org.joe_e.*;

public class ConstructPowerlessLocal1 {
	static class Inner implements Powerless {
		class MoreInner implements Powerless {
			void method() {
				final String s = new String();
				
				new Powerless() {
					public String toString() {
						if (s == null) {
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
