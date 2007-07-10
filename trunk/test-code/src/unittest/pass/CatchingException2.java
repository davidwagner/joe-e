package unittest.pass;

public class CatchingException2 {
	Object again;
	
	void foo() throws Throwable {
		try {
			try {
				try {
					again = new Object();
				} catch (ArithmeticException ae) {
					throw ae;
				}		
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			
		}
	}
}
