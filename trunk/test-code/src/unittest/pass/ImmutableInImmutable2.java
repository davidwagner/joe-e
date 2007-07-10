package unittest.pass;

class ImmutableType implements org.joe_e.Immutable {
	
}

public class ImmutableInImmutable2 extends ImmutableType {
	void foo() {
		new ImmutableType() {
			
		};
	}
}

