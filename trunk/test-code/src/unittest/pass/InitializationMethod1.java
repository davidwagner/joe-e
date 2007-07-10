package unittest.pass;

public class InitializationMethod1 {
	void foo() {
		class B {
			class C {
				Object o = toasty();
				public String toString() {
					return o == null? "a" : "b";
				}
			}
		}
	}
	
	public static String toasty() {
		return "yum, toast";
	}
}
