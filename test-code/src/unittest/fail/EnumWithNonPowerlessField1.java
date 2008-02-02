package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonPowerlessField1 implements Powerless, Equatable {
	a, b, c;
	
	final Token notPowerless = new Token();
	
	Token getNotPowerless() {
		return notPowerless;
	}
}
