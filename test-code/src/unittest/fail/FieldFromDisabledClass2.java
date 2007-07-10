package unittest.fail;

public class FieldFromDisabledClass2 extends ReferencesDisabledClass5a {
	Object foo2() {
		return i;  
		// error is redundant since its from a supertype, but hey, it works.
	}
}
