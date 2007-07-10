package unittest.pass;

interface Foo2 extends org.joe_e.Immutable, org.joe_e.Equatable {
}

public class TypeParameterIdentity1 {
	<T extends Foo2> boolean foo(T t) {
		return t == new Object();
	}
}
