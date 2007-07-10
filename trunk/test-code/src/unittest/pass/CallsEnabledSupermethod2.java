package unittest.pass;

import org.joe_e.Powerless;

public class CallsEnabledSupermethod2 extends Exception implements Powerless {
	static final long serialVersionUID = 1;
	
	void foo2() {
		super.equals(null);
	}
}
