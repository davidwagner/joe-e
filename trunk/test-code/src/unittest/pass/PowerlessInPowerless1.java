package unittest.pass;

import org.joe_e.*;

public class PowerlessInPowerless1 implements Powerless {
	class Inner implements org.joe_e.Powerless {
		final Exception e = null;
		final String s = null;
	}
}