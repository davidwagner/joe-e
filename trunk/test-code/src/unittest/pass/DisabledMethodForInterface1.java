package unittest.pass;

import org.joe_e.Powerless;

interface Printable {
	public String toString();
}

public class DisabledMethodForInterface1 extends Exception 
									    implements Powerless, Printable {
	static final long serialVersionUID = 1;
	
	public boolean equals(Object o) {
		return false;
	}
}
