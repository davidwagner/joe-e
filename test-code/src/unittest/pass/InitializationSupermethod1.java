package unittest.pass;

public class InitializationSupermethod1 {
	static class A {
		static boolean equalz(Object o) {
			return false;
		}
	}
	
	void foo() {
		class B {
			class C extends A {
				boolean b = super.equalz(null);
				public String toString() {
					return b? "a" : "b";
				}
			}
		}
	}
}
