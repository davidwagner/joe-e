package unittest.pass;

public class MethodFromEnabledClass2 extends ReferencesEnabledClass5a {
	void foo2() {
		getString();  
		// error is redundant since its from a supertype, but hey, it works.
	}
}
