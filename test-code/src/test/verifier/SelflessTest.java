package test.verifier;

import org.joe_e.Selfless;

public class SelflessTest implements Selfless {
	int peep; // error
	
	public boolean equals(Object o) {
		return false;
	}
	
	//public int hashCode() {
	//	return 5;
	//}
}
