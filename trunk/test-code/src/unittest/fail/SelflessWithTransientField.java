package unittest.fail;

interface SelflessPlus2 extends org.joe_e.Selfless {}

public class SelflessWithTransientField implements SelflessPlus2 {
	final Exception e = null;
	final transient String s = null;
	
	public boolean equals(Object o) {
		return false;
	}
	
	public int hashCode() {
		return s.hashCode();
	}
}

