package unittest.pass;

import org.joe_e.Powerless;

public class StaticFields1 implements Powerless {
	static final int foo = 52;
	static final StaticFields1 sf1 = new StaticFields1();
	static final Powerless p = sf1;
	final static long looong = 231;
	final static org.joe_e.array.PowerlessArray pa = null;
}
