package unittest.pass;

import org.joe_e.array.ConstArray;
import org.joe_e.Powerless;

public class NonImmutableLocal3 implements Powerless {
	void method() {
		class A {
			class B {
				class C {
					final ConstArray<Object> cao = 
						ConstArray.<Object>array(1, 2, 3);
					class D {
						public String toString() {
							return ((Integer) (cao.get(1))).toString();
						}
					}
				}
			}
		}
		
		class E implements Powerless {
			public String toString() {
				return "" + new A().new B().new C().new D();
			}
		}
	}
}
