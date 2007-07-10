package unittest.pass;

import org.joe_e.array.ImmutableArray;
import org.joe_e.Immutable;

public class InheritedImmutableLocal2 implements Immutable {
	void method() {
		final ImmutableArray<Object> cao = 
			ImmutableArray.<Object>array(1, 2, 3);
	
		class A {
			class B {
				class C {
					class D {
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
		
		class F extends E implements Immutable {
			
		}
	}
}

