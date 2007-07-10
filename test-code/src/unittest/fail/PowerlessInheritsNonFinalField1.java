package unittest.fail;

import org.joe_e.testlib.ContainsNonFinal;

public class PowerlessInheritsNonFinalField1 extends ContainsNonFinal 
										implements org.joe_e.Powerless {
	final Exception e = null;
	final String s = null;
}
