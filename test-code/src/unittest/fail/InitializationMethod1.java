package unittest.fail;

public class InitializationMethod1 {
	void foo() {
		class B {
			class C {
				Object o = toString();
				public String toString() {
					return o == null? "a" : "b";
				}
			}
		}
	}
}
