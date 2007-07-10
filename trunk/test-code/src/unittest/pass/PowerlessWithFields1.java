package unittest.pass;

import org.joe_e.array.PowerlessArray;

class PowerlessType implements org.joe_e.Powerless {

}

public class PowerlessWithFields1 extends PowerlessType {
	final Exception e = null;
	final String s = null;
	final PowerlessArray<Object> pao = null;
	final Throwable t = null;
	final int f = 5;
	final float pi = 3.14f;
}
