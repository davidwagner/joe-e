package unittest.fail;

import org.joe_e.testlib.ContainsNonFinal;

public class ImmutableInheritsNonFinalField1 extends ContainsNonFinal 
										implements org.joe_e.Immutable {
	final Exception e = null;
	final String s = null;
}
