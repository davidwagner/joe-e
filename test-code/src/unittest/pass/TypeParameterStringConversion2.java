package unittest.pass;

import org.joe_e.testlib.HasEnabledToString;

public class TypeParameterStringConversion2 {
	<T extends HasEnabledToString> String foo(T t) {
		String a = "a";
		a += t;
		return a;
	}
}
