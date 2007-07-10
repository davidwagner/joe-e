package unittest.fail;

class ImmutableType implements org.joe_e.Immutable {
	
}

public class ImmutableInNonImmutable2 {
	void foo() {
		new ImmutableType() {
			
		};
	}
}

