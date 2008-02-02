package unittest.fail;

// at least for now, Selfless doesn't make toString safe
// (but it does hashCode, which is used in Object's toString...)
interface Foo7 extends org.joe_e.Immutable, org.joe_e.Selfless {
}

public class TypeParameterStringConversion7 {
	<T extends Foo7> String foo(T t) {
		assert false : t;
		return null;
	}
}
