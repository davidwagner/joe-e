package unittest.pass;

import org.joe_e.*;

public class PowerlessWithFields4<E extends Throwable> 
										implements Powerless {
	final int f = 4;
	final String s = "";
	final E e = null;
			
	public String toString() {
		return (e == null) ? f + s : "b";
	}
}
