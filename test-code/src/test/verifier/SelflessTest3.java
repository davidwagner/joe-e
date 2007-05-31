package test.verifier;

public class SelflessTest3 extends SelflessTest2 {
	static class E {
		void m() {
			final int[] arr = new int[]{0};
		    class C {
			    class D implements org.joe_e.Immutable {
				    void n() {
					    class F implements org.joe_e.Powerless {
						    F() {
							    System.out.println("rube goldberg " + ++arr[0]);
						    }
					    }
					
					    new F();
				    }
				
				    D() {
					    n();
					    ++arr[0];
				    }
			    }
			
			    C() {
				    new D();
			    }
		    }
		
  		    new C();
	    }
	}
}
