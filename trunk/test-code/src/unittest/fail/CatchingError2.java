package unittest.fail;

public class CatchingError2 {
	Object again;
	
	void foo() throws Throwable {
		try {
			try {
				try {
					again = new Object();
				} catch (java.lang.AssertionError ae) {
					throw ae;
				}		
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			
		}
	}
}
