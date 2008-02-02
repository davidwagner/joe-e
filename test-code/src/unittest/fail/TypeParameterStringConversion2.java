package unittest.fail;

public class TypeParameterStringConversion2 {
	<T> String foo(T t) {
		String a = "a";
		a += t;
		return a;
	}
}
