package unittest.pass;

public class SelflessOverridesEquals implements org.joe_e.Selfless {
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(Object o) {
		return true;
	}
}
