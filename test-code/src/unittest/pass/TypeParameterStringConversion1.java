package unittest.pass;

interface Foo extends org.joe_e.Immutable, org.joe_e.Selfless {
	public String toString();
}

public class TypeParameterStringConversion1 {
	<T extends Foo> String foo(T t) {
		return t + "";
	}
}
