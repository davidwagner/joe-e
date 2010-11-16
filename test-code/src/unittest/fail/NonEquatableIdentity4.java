package unittest.fail;

import org.joe_e.Token;
import org.joe_e.array.PowerlessArray;

public class NonEquatableIdentity4 {
	boolean revealsStringIdentity(String a, String b) {
		PowerlessArray<String> pas = PowerlessArray.array(a);
		PowerlessArray<Token> pat = (PowerlessArray<Token>) (PowerlessArray) pas; 
		// OK by Joe-E, but will fail with ClassCastException
		boolean result1 = ((Token) pat.get(0)) == (Object) b;
		// Not OK; pat.get(0) has erased type Object
		boolean result2 = pat.get(0) == (Object) b;
		return result1 ^ result2;
	}
	String b = "foo";
	String a = b;
	
}