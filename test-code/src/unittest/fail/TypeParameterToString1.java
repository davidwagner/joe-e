package unittest.fail;

interface Foo extends org.joe_e.Immutable, org.joe_e.Selfless {
}

public class TypeParameterToString1 {
	<T extends Foo> String foo(T t) {
		return t + "";
	}
}
