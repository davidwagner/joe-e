package unittest.fail;

import org.joe_e.*;

public class InheritedNonPowerlessLocal1 {
	static void method() {
		final Token t = new Token();

		class Inner implements Powerless {
			class MoreInner implements Powerless {
				class Super {
					public String toString() {
						if (t == null) {
							return "a";
						} else {
							return "b";
						}
					}
				}
			}
		}
		
		class Sub extends Inner.MoreInner.Super implements Powerless {
			Sub() {
				new Inner().new MoreInner().super();
			}
		}
	}
}
