package unittest.fail;

public class CatchingThrowable2 {
	Object again;
	
	void foo() throws Throwable {
		try {
			try {
				try {
					again = new Object();
				} catch (java.lang.Throwable t) {
					throw t;
				}		
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			
		}
	}
}
