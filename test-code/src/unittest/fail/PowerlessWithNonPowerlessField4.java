package unittest.fail;

import org.joe_e.*;

public class PowerlessWithNonPowerlessField4<E extends Immutable & Powerless> 
										implements Powerless {
	final int f = 4;
	final String s = "";
	final E e = null;
			
	public String toString() {
		return (e == null) ? f + s : "b";
	}
}
