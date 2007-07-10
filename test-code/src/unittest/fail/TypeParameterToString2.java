package unittest.fail;

public class TypeParameterToString2 {
	<T> String foo(T t) {
		String a = "a";
		a += t;
		return a;
	}
}
