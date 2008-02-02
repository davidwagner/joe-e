package unittest.fail;

import org.joe_e.*;

public enum EnumWithNonFinalField2 implements Powerless, Equatable {
	a, b, c {
		int five = 17;
		public int getFive() {
			return five;
		}
	};
	
	public int getFive () {
		return 5;
	}
}
