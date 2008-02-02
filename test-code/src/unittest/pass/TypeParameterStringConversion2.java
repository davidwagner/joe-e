package unittest.pass;

public class TypeParameterStringConversion2 {
	<T extends Exception> String foo(T t) {
		String a = "a";
		a += t;
		return a;
	}
}
