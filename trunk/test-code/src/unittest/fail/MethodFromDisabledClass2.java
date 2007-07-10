package unittest.fail;

public class MethodFromDisabledClass2 extends ReferencesDisabledClass5a {
	void foo2() {
		foo();  
		// error is redundant since its from a supertype, but hey, it works.
	}
}
