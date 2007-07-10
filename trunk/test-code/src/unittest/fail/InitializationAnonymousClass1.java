package unittest.fail;

public class InitializationAnonymousClass1 {
	void foo() {
		class B {
			class C {
				Object o = new Object() {};
				public String toString() {
					return o == null? "a" : "b";
				}
			}
		}
	}
}
