package unittest.pass;

public class CallsEnabledSupermethod1 {
	class A {
		
	}
	
	class B extends A {
		public String toString() {
			return super.equals(null) ? "a" : "b";
		}
	}
}
