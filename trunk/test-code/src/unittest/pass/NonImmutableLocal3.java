package unittest.pass;

import org.joe_e.array.ConstArray;
import org.joe_e.Powerless;

public class NonImmutableLocal3 implements Powerless {
	void method() {
		class A {
            class B {
                void f() {
                    final ConstArray<Object> cao = 
                        ConstArray.<Object>array(1, 2, 3);
                    class C {
                        class D {
                            public String toString() {
                                return ((Integer) (cao.get(1))).toString();
                            }
                        }
                    }
                    new C().new D();
                }
            }
            B makeB() {
                return new B();
            }
		}
		
        new A();
	}
}
