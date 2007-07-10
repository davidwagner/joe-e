package unittest.fail;

public interface StaticNonPowerless5 {
	// interface members are implicitly static final,
	// so there is only one error here.
	class Inner {}
	
	Inner i = new Inner();
}
