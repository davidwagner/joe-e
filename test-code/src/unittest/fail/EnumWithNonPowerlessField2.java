package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonPowerlessField2 implements Powerless, Equatable {
	a, b, c {
		final Token tok = new Token();
		public Token getTok() {
			return tok;
		}
	};
	
	public Token getTok () {
		return new Token();
	}
}
