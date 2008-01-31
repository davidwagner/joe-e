package unittest.pass;

import org.joe_e.array.PowerlessArray;

class PowerlessType implements org.joe_e.Powerless {

}

public class PowerlessWithFields1 extends PowerlessType {
	public final Exception e = null;
	public final String s = null;
	public final PowerlessArray<Object> pao = null;
	final Throwable t = null;
	final int f = 5;
	final float pi = 3.14f;
}
