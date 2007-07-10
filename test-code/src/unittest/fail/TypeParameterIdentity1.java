package unittest.fail;

interface Foo2 extends org.joe_e.Immutable, org.joe_e.Selfless {
}

public class TypeParameterIdentity1 {
	// Only first bound counts, as it is reflected in erasure
	<T extends Foo2 & org.joe_e.Equatable> boolean foo(T t) {
		return t == new Object();
	}
}
