package unittest.pass;

import org.joe_e.array.PowerlessArray;
import org.joe_e.Powerless;

public class PowerlessLocal2 implements Powerless {
	void method() {
		final PowerlessArray<Object> pao = 
			PowerlessArray.<Object>array(1, 2, 3);
	
		class A implements Powerless {
			class B implements Powerless {
				class C implements Powerless {
					class D implements Powerless {
						public String toString() {
							return ((Integer) (pao.get(1))).toString();
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
