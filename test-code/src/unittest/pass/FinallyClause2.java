package unittest.pass;

public class FinallyClause2 {
	Object again;
	
	void foo() {
		RuntimeException re = null;
		try {
			try {		
				new String();
			} catch (ArithmeticException ae) {
				// specific exception handling
			}
		} 
		catch (RuntimeException rex) {
			re = rex;
		}
		
		// finally action
				
		if (re != null) throw re;
	}
}