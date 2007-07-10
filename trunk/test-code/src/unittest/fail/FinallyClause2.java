package unittest.fail;

public class FinallyClause2 {
	Object again;
	
	void foo() throws Throwable {
		try {
			try {
				try {
					again = new Object();
				} finally {
					again = null;
				}		
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			
		}
	}
}
