package unittest.fail;

import org.joe_e.array.ConstArray;
import org.joe_e.Powerless;

public class NonImmutableLocal2 implements Powerless {
	void method() {
		final ConstArray<Object> cao = ConstArray.<Object>array(1, 2, 3);
	
		class A implements Powerless {
			class B implements Powerless {
				class C implements Powerless {
					class D implements Powerless {
						public String toString() {
							return ((Integer) (cao.get(1))).toString();
						}
					}
				}
			}
		}
		
		class E {
			public String toString() {
				return "" + new A().new B().new C().new D();
			}
		}
		
		class F extends E implements Powerless {
			
		}
	}
}
