package unittest.pass;

import org.joe_e.array.PowerlessArray;
import org.joe_e.Powerless;

public class ConstructPowerlessLocal2 implements Powerless {
	void method() {
		final PowerlessArray<Object> cao = 
			PowerlessArray.<Object>array(1, 2, 3);
	
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
