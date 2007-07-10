package unittest.pass;

public class TypeParameterToString2 {
	<T extends Exception> String foo(T t) {
		String a = "a";
		a += t;
		return a;
	}
}
