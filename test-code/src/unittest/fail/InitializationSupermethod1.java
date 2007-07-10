package unittest.fail;

public class InitializationSupermethod1 {
	void foo() {
		class B {
			class C {
				boolean b = super.equals(null);
				public String toString() {
					return b? "a" : "b";
				}
			}
		}
	}
}
