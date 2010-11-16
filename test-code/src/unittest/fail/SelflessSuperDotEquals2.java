package unittest.fail;

public class SelflessSuperDotEquals2 implements org.joe_e.Selfless {
	public int hashCode() {
		return 42;
	}
	
	public boolean equals(final Object other) {
		return new Object() {
			boolean foo () {
				return SelflessSuperDotEquals2.super.equals(other);
			}
		}.foo();
	}
}
