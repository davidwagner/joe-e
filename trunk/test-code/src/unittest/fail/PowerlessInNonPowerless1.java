package unittest.fail;

public class PowerlessInNonPowerless1 implements org.joe_e.Immutable {
	class Inner implements org.joe_e.Powerless {
		final Exception e = null;
		final String s = null;
	}
}