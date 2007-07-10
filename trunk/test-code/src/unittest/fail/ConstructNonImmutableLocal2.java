package unittest.fail;

import org.joe_e.array.ConstArray;
import org.joe_e.Powerless;

public class ConstructNonImmutableLocal2 implements Powerless {
	void method(final ConstArray<Object> cao) {
		class A implements Powerless {
			class B implements Powerless {
				class C implements Powerless {
					class D {
						public String toString() {
							return ((Integer) (cao.get(1))).toString();
						}
					}
				}
			}
			Object getStuff() {
				return new B().new C().new D();
			}
		}
		
		new A().getStuff();
	}
}
