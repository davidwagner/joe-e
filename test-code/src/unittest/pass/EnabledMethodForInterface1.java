package unittest.pass;

import org.joe_e.Powerless;
import org.joe_e.testlib.HasEnabledToString;

interface Printable {
	public String toString();
}

public class EnabledMethodForInterface1 extends HasEnabledToString
									    implements Powerless, Printable {
	static final long serialVersionUID = 1;
	
	public boolean equals(Object o) {
		return false;
	}
}
