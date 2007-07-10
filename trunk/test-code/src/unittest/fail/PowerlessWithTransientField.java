package unittest.fail;

interface PowerlessPlus3 extends org.joe_e.Powerless {}

public class PowerlessWithTransientField implements PowerlessPlus3 {
	final Exception e = null;
	final transient String s = null;
	
	public boolean equals(Object o) {
		return false;
	}
	
	public int hashCode() {
		return s.hashCode();
	}
}

