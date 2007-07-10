package unittest.fail;

import org.joe_e.Selfless;

public class DisabledMethodForInterface1 implements Selfless {
	public boolean equals(Object o) {
		return false;
	}
}
