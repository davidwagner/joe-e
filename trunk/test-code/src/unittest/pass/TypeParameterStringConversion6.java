package unittest.pass;

interface Foo6 extends org.joe_e.Immutable, org.joe_e.Selfless {
	public String toString();
}

public class TypeParameterStringConversion6 {
	<T extends Foo6> void foo(T t) {
		assert false : t;
	}
}
