package unittest.pass;

import org.joe_e.*;

public class InheritedPowerlessLocal {
	static void method() {
		final Powerless p = null;

		class Inner implements Powerless {
			class MoreInner implements Powerless {
				class Super {
					public String toString() {
						if (p == null) {
							return "a";
						} else {
							return "b";
						}
					}
				}
			}
		}
		
		class Sub extends Inner.MoreInner.Super implements Immutable {
			Sub() {
				new Inner().new MoreInner().super();
			}
		}
	}
}
