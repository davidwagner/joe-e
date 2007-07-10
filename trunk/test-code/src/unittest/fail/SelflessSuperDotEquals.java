package unittest.fail;

public class SelflessSuperDotEquals implements org.joe_e.Selfless {
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(Object other) {
		return super.equals(other);
	}
}
