package unittest.fail;

public class CallsDisabledSupermethod1 {
	class A {
		
	}
	
	class B extends A {
		public String toString() {
			return super.toString();
		}
	}
}
