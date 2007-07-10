package unittest.pass;

import org.joe_e.array.PowerlessArray;
import org.joe_e.Powerless;

class Thingie3 {
	final PowerlessArray<Object> pao = PowerlessArray.<Object>array(1, 2, 3);
	final boolean fiver = (5 % 2) == 1;
}

public class PowerlessInheritsFields2 extends Thingie3
										implements Powerless {
	final Exception e = null;
	final String s = null;
}

